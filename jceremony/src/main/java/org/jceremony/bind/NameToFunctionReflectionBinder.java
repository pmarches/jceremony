package org.jceremony.bind;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

import org.jceremony.model.LifecycleFunction;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.PerformTransitionContext;

public class NameToFunctionReflectionBinder {
  static final Class<?>[] parameterTypesOfTheFunctionalInterface;
  
  static {
    Class<LifecycleFunction> theFunctionalInterface=LifecycleFunction.class;
    parameterTypesOfTheFunctionalInterface = theFunctionalInterface.getMethods()[0].getParameterTypes();
  }

  protected LifecycleFunction convertMethodToLifecycleFunction(final Object instance, final Method method) {
    return new LifecycleFunction() {
      @Override
      public void invoke(String functionName, LifecycleState departureState, LifecycleState arrivalState, PerformTransitionContext executionContext) throws Exception {
        method.invoke(instance, functionName, departureState, arrivalState, executionContext);
      }
    };
  }
  
  public void discoverBindingsByReflection(Object implementation, NameToFunctionBindings bindings) {
    Objects.requireNonNull(implementation);
    Objects.requireNonNull(bindings);
    
    Class theClassUnderInspection=implementation.getClass();
    Arrays.stream(theClassUnderInspection.getMethods())
      .filter(m-> (m.getModifiers()&Modifier.PUBLIC)!=0)
      .filter(m-> m.getReturnType().equals(Void.TYPE))
      .filter(m-> Arrays.equals(m.getParameterTypes(), parameterTypesOfTheFunctionalInterface))
      .forEach(m->bindings.bindNameToFunction(m.getName(), convertMethodToLifecycleFunction(implementation, m)));
  }
}
