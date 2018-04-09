package org.jceremony.bind;

import java.util.HashMap;

import org.jceremony.model.LifecycleFunction;

public class NameToFunctionBindings extends LifecycleFunctionalBindings {
  protected HashMap<String, LifecycleFunction> nameToFunction=new HashMap<>();
  
  @Override
  public LifecycleFunction get(String functionName) {
    LifecycleFunction functionToInvoke=nameToFunction.get(functionName);
    return functionToInvoke;
  }

  public void bindNameToFunction(String functionName, LifecycleFunction function) {
    nameToFunction.put(functionName, function);
  }
}
