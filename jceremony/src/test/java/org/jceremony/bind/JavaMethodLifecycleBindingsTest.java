package org.jceremony.bind;

import org.jceremony.model.LifecycleState;
import org.jceremony.model.PerformTransitionContext;
import org.junit.Test;

public class JavaMethodLifecycleBindingsTest {
  private void someTransition(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) {
  }
  
  @Test
  public void testMapJavaMethodStringLifecycleFunction() {
    NameToFunctionBindings binding1=new NameToFunctionBindings();
    binding1.bindNameToFunction("transition1", this::someTransition);
  }

}
