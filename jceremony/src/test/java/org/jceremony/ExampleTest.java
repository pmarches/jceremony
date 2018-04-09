package org.jceremony;

import static org.junit.Assert.assertTrue;

import org.jceremony.bind.NameToFunctionReflectionBinder;
import org.jceremony.bind.NameToFunctionBindings;
import org.jceremony.model.LifecycleModel;
import org.jceremony.model.LifecycleModelBuilder;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.StateName;
import org.jceremony.model.Statusable;
import org.jceremony.model.PerformTransitionContext;
import org.junit.Test;

public class ExampleTest {
  public void checkArrivalB1Method(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception{
  }

  static public class CodeToExecuteDuringTransitions {
    public void performAfterArrivalB1(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception{
    }
    public void checkCanDepartureA(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception{
    }
    public void performAfterDepartureA(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception{
    }
  }
  
  static class MyObjectWithCompositeStatus {
    String state1="a";
    int state2=1;
    
    Statusable statusable1=new Statusable() {
      @Override
      public void setNewStatus(StateName newStatus) {
        //If this method throws an exception, the 
        MyObjectWithCompositeStatus.this.state1=newStatus.toString();
      }
      
      @Override
      public StateName getCurrentStateName() {
        return new StateName(MyObjectWithCompositeStatus.this.state1);
      }
    };
  }
  
  @Test
  public void test() throws Exception {
    //You can build the lifecycle using the builder, or load it from a file
    LifecycleModel abLifeCycle = new LifecycleModelBuilder()
        .state("a") //The first state defined will always be sourced from "__init__"
          .addCheckIfCanDepart("checkCanDepartureA")
          .addUponDeparture("performAfterDepartureA")
          .to("b")
        .state("b")
          .addUponArrival("performAfterArrivalB1")
          .addCheckIfCanArrive("checkArrivalB1") //Function names are scoped to a lifecycle, not just a state. This allows re-using functions
        .builder()
      .build();
    
    //The bindings map a function name to a function.
    NameToFunctionBindings bindings=new NameToFunctionBindings();

    //You can bind a function either manually like this:
    bindings.bindNameToFunction("checkArrivalB1", this::checkArrivalB1Method);

    //OR by using reflection, the method name must match the lifecycle function name and the method signature must match that of LifecycleFunction.invoke(...)
    CodeToExecuteDuringTransitions reflectionBassedImplementation=new CodeToExecuteDuringTransitions();
    new NameToFunctionReflectionBinder().discoverBindingsByReflection(reflectionBassedImplementation, bindings);
    
    MyObjectWithCompositeStatus principalObject=new MyObjectWithCompositeStatus();
    PerformTransitionContext transitionContext=new PerformTransitionContext(principalObject.statusable1, abLifeCycle, bindings);
    new PerformTransition().transitionTo(transitionContext, new StateName("b"));

    //Any exceptions occuring during the check phase would have been thrown normally. 
    //But any exception occuring after the check phase will be stored in the execution context.
    assertTrue(transitionContext.getExceptionDuringPerformFunctions().isEmpty());
  }

}
