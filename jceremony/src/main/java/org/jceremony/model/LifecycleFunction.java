package org.jceremony.model;

public interface LifecycleFunction {
  public void invoke(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception;
}
