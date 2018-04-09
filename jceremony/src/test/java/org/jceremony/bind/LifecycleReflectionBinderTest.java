package org.jceremony.bind;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;

import org.jceremony.PerformTransition;
import org.jceremony.model.LifecycleModel;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.StateName;
import org.jceremony.model.Statusable;
import org.jceremony.model.PerformTransitionContext;
import org.jceremony.model.WellKnownLifecycles;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class LifecycleReflectionBinderTest {

  static interface ABCBindings {
    public void checkArrivalA1(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void checkCanDepartureA(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterDepartureA(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalB1(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalB2(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalB3(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void checkCanArrivalB1(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalC1(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalC2(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void checkCanDepartureC(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
    public void performAfterArrivalA2(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext);
  }
  
  @Test
  public void test() throws Exception {
    LifecycleModel abcdLifeCycle = WellKnownLifecycles.abcdLifecycle().build();
    Statusable instanceAtStateA = Mockito.mock(Statusable.class);
    Mockito.when(instanceAtStateA.getCurrentStateName()).thenReturn(new StateName("a"));

    ABCBindings mockAbcdJavaImpl = Mockito.mock(ABCBindings.class);
    NameToFunctionBindings bindings=new NameToFunctionBindings();
    new NameToFunctionReflectionBinder().discoverBindingsByReflection(mockAbcdJavaImpl, bindings);

    PerformTransitionContext executionContext = new PerformTransitionContext(instanceAtStateA, abcdLifeCycle, bindings);
    new PerformTransition().transitionTo(executionContext, new StateName("b"));
    
    InOrder inOrder = Mockito.inOrder(mockAbcdJavaImpl);
    inOrder.verify(mockAbcdJavaImpl).checkCanDepartureA(eq("checkCanDepartureA"), notNull(), notNull(), same(executionContext));
    inOrder.verify(mockAbcdJavaImpl).checkCanArrivalB1(eq("checkCanArrivalB1"), notNull(), notNull(), same(executionContext));
    inOrder.verify(mockAbcdJavaImpl).performAfterDepartureA(eq("performAfterDepartureA"), notNull(), notNull(), same(executionContext));
    inOrder.verify(mockAbcdJavaImpl).performAfterArrivalB1(eq("performAfterArrivalB1"), notNull(), notNull(), same(executionContext));
    inOrder.verify(mockAbcdJavaImpl).performAfterArrivalB2(eq("performAfterArrivalB2"), notNull(), notNull(), same(executionContext));
    inOrder.verify(mockAbcdJavaImpl).performAfterArrivalB3(eq("performAfterArrivalB3"), notNull(), notNull(), same(executionContext));
    inOrder.verifyNoMoreInteractions();
    assertEquals(0, executionContext.getExceptionDuringPerformFunctions().size());

  }

}
