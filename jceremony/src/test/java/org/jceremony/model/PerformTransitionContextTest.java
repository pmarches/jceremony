package org.jceremony.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;

import org.jceremony.PerformTransition;
import org.jceremony.bind.LifecycleFunctionalBindings;
import org.jceremony.bind.NameToFunctionBindings;
import org.jceremony.model.LifecycleFunction;
import org.jceremony.model.LifecycleModel;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.PerformTransitionContext;
import org.jceremony.model.StateName;
import org.jceremony.model.Statusable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class PerformTransitionContextTest {
  LifecycleModel abcd;
  Statusable instanceAtStateA;
  LifecycleFunction mockCatchAllFunction;
  private PerformTransitionContext abcdExecutionContext;
  
  @Before
  public void setup() throws Exception {
    abcd = WellKnownLifecycles.abcdLifecycle().build();
    instanceAtStateA=Mockito.mock(Statusable.class);
    Mockito.when(instanceAtStateA.getCurrentStateName()).thenReturn(new StateName("a"));

    LifecycleFunctionalBindings bindings = Mockito.mock(LifecycleFunctionalBindings.class);
    mockCatchAllFunction=Mockito.mock(LifecycleFunction.class);
    Mockito.when(bindings.get(anyString())).thenReturn(mockCatchAllFunction);

    abcdExecutionContext=new PerformTransitionContext(instanceAtStateA, abcd, bindings);
  }

  @Test
  public void testHappyPathExecution() throws Exception {
    new PerformTransition().transitionTo(abcdExecutionContext, new StateName("b"));
    
    InOrder inOrder = Mockito.inOrder(mockCatchAllFunction, instanceAtStateA);
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanArrivalB1"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(instanceAtStateA).setNewStatus(eq(new StateName("b")));
    inOrder.verify(mockCatchAllFunction).invoke(eq("performAfterDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction, times(3)).invoke(startsWith("performAfterArrivalB"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verifyNoMoreInteractions();
    assertEquals(0, abcdExecutionContext.getExceptionDuringPerformFunctions().size());
  }
  
  @Test
  public void testWhenSecondCheckThrowsException() throws Exception{
    Exception theExceptionThrownByTheCheckFunction=new Exception("XXX");
    try{
      Mockito.doNothing().when(mockCatchAllFunction).invoke(anyString(), notNull(), notNull(), same(abcdExecutionContext));
      Mockito.doThrow(theExceptionThrownByTheCheckFunction).when(mockCatchAllFunction).invoke(eq("checkCanArrivalB1"), notNull(), notNull(), same(abcdExecutionContext));
      new PerformTransition().transitionTo(abcdExecutionContext, new StateName("b"));
      fail();
    }
    catch(Exception e){
      if(e != theExceptionThrownByTheCheckFunction) {
        fail();
      }
    }

    InOrder inOrder = Mockito.inOrder(mockCatchAllFunction, instanceAtStateA);
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanArrivalB1"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testChecksAreOkButPerformThrowsException() throws Exception{
    try{
      Mockito.doNothing().when(mockCatchAllFunction).invoke(anyString(), notNull(), notNull(), same(abcdExecutionContext));
      Mockito.doThrow(new RuntimeException("XXX")).when(mockCatchAllFunction).invoke(eq("performAfterArrivalB2"), any(), any(), same(abcdExecutionContext));
      new PerformTransition().transitionTo(abcdExecutionContext, new StateName("b"));
    }
    catch(Throwable e){
      fail();
    }

    InOrder inOrder = Mockito.inOrder(mockCatchAllFunction, instanceAtStateA);
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanArrivalB1"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(instanceAtStateA).setNewStatus(eq(new StateName("b")));
    inOrder.verify(mockCatchAllFunction).invoke(eq("performAfterDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction, times(3)).invoke(startsWith("performAfterArrivalB"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verifyNoMoreInteractions();
    assertEquals(1, abcdExecutionContext.getExceptionDuringPerformFunctions().size());
  }

  @Test
  public void testWhenPerformFunctionIsNotBoundPromotionDoesNotOccur() throws Exception {
    LifecycleModel ab = WellKnownLifecycles.abLifecycle().build();
    NameToFunctionBindings bindings=new NameToFunctionBindings();
    LifecycleFunction noop=new LifecycleFunction() {
      @Override
      public void invoke(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception {
      }
    };
    bindings.bindNameToFunction("checkCanDepartureA", noop);
    bindings.bindNameToFunction("checkCanArrivalB1", noop);
    bindings.bindNameToFunction("performAfterDepartureA", noop);
    bindings.bindNameToFunction("performAfterDepartureB1", noop);
    
    PerformTransitionContext abExecutionContext = new PerformTransitionContext(instanceAtStateA, ab, bindings);
    try {
      new PerformTransition().transitionTo(abExecutionContext, new StateName("b"));
      fail();
    } catch (Exception e) {
      assertEquals(e.getMessage(), "The function named 'performAfterArrivalB1' needs to be bound before this transition can be invoked");
    }    
    bindings.bindNameToFunction("performAfterArrivalB1", noop);
    new PerformTransition().transitionTo(abExecutionContext, new StateName("b"));
  }
  
  @Test
  public void testExcceptionDuringStatusChangeGetHandled() throws Exception {
    RuntimeException stateChangeException=new RuntimeException("Something went wrong during state change");
    try{
      Mockito.doNothing().when(mockCatchAllFunction).invoke(anyString(), notNull(), notNull(), same(abcdExecutionContext));
      Mockito.doThrow(stateChangeException).doNothing().when(instanceAtStateA).setNewStatus(any());
      new PerformTransition().transitionTo(abcdExecutionContext, new StateName("b"));
      fail();
    }
    catch(Throwable e){
      assertSame(e, stateChangeException);
    }
    
    InOrder inOrder = Mockito.inOrder(mockCatchAllFunction, instanceAtStateA);
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanDepartureA"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(mockCatchAllFunction).invoke(eq("checkCanArrivalB1"), notNull(), notNull(), same(abcdExecutionContext));
    inOrder.verify(instanceAtStateA).setNewStatus(eq(new StateName("b")));
    inOrder.verifyNoMoreInteractions();
    assertEquals(0, abcdExecutionContext.getExceptionDuringPerformFunctions().size());
  }

}
