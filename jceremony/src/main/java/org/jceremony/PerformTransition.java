package org.jceremony;

import java.util.List;
import java.util.Objects;

import org.jceremony.model.LifecycleFunction;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.StateName;
import org.jceremony.model.PerformTransitionContext;

public class PerformTransition {
  public void transitionTo(PerformTransitionContext executionContext, StateName targetStateName) throws Exception {
    Objects.requireNonNull(executionContext, "Execution context cannot be null");
    Objects.requireNonNull(targetStateName, "The target state name cannot be null");

    StateName currentStateName=executionContext.getStatusable().getCurrentStateName();
    LifecycleState departureState=executionContext.getLifecycle().lookupStateFromLifecycleOrThrowIfNotExists(currentStateName);
    LifecycleState arrivalState = executionContext.getLifecycle().lookupStateFromLifecycleOrThrowIfNotExists(targetStateName);
    
    ensureFunctionsHaveBeenBound(departureState.uponDepartedFromThisState, executionContext);
    ensureFunctionsHaveBeenBound(arrivalState.uponArrivedToThisState, executionContext);
    
    performCheckBeforeTransitionOccurs(departureState.checkIfThisStateCanBeDeparted, executionContext, departureState, arrivalState);
    performCheckBeforeTransitionOccurs(arrivalState.checkIfThisStateCanBeArrived, executionContext, departureState, arrivalState);

    executionContext.getStatusable().setNewStatus(targetStateName);
    invokePerformFunctionsAndSendExceptionsToExecutionContext(departureState.uponDepartedFromThisState, executionContext, departureState, arrivalState);
    invokePerformFunctionsAndSendExceptionsToExecutionContext(arrivalState.uponArrivedToThisState, executionContext, departureState, arrivalState);
  }

  protected void ensureFunctionsHaveBeenBound(List<String> allFunctionNamesToCheck, PerformTransitionContext executionContext) throws Exception {
    for(String functionName:allFunctionNamesToCheck){
      if(executionContext.getBindings().get(functionName)==null) {
        String msg=String.format("The function named '%s' needs to be bound before this transition can be invoked", functionName);
        throw new Exception(msg);
      }
    }
  }

  protected void invokePerformFunctionsAndSendExceptionsToExecutionContext(List<String> functionsToInvoke, PerformTransitionContext executionContext, LifecycleState departureState, LifecycleState arrivalState) {
    for(String performName:functionsToInvoke){
      try {
        LifecycleFunction functionToInvoke = executionContext.getBindings().get(performName);
        if(functionToInvoke==null) {
          //This should never occur because ensureAllFunctionsCanBeCalled shold have checked all this before
          String msg=String.format("In invokePerformFunctionsAndSendExceptionsToExecutionContext, failed to resolve function named '%s'", performName);
          throw new Exception(msg);
        }
        functionToInvoke.invoke(performName, departureState, arrivalState, executionContext);
      } catch (Throwable e) {
        executionContext.addExceptionThatOccuredDuringPerformPhase(e);
      }
    }
  }

  protected void performCheckBeforeTransitionOccurs(List<String> checkNames, PerformTransitionContext executionContext, LifecycleState departureState, LifecycleState arrivalState) throws Exception {
    for(String checkName:checkNames){
      LifecycleFunction functionToInvoke = executionContext.getBindings().get(checkName);
      if(functionToInvoke==null) {
        String msg=String.format("In performCheckBeforeTransitionOccurs, failed to resolve function named '%s'", checkName);
        throw new Exception(msg);
      }
      functionToInvoke.invoke(checkName, departureState, arrivalState, executionContext);
    }
  }
}
