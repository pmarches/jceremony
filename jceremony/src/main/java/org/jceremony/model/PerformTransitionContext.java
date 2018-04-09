package org.jceremony.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jceremony.bind.LifecycleFunctionalBindings;

public class PerformTransitionContext {
  Statusable statusableInstance;
  LifecycleModel lifecycleModel;
  LifecycleFunctionalBindings bindings;
  List<Throwable> exceptionsEncounteredDuringPerformPhase=new ArrayList<>();

  public PerformTransitionContext(Statusable statusableInstance, LifecycleModel lifecycleModel, LifecycleFunctionalBindings bindings) {
    this.statusableInstance=Objects.requireNonNull(statusableInstance);
    this.lifecycleModel=Objects.requireNonNull(lifecycleModel);
    this.bindings=Objects.requireNonNull(bindings);
  }

//  public void getHistory(){
//    //Can be used to re-order functions
//  }
  
  public Statusable getStatusable(){
    return statusableInstance;
  }

  public LifecycleModel getLifecycle() {
    return lifecycleModel;
  }

  public LifecycleFunctionalBindings getBindings() {
    return bindings;
  }

  public void addExceptionThatOccuredDuringPerformPhase(Throwable e) {
    exceptionsEncounteredDuringPerformPhase.add(e);
  }

  public List<Throwable> getExceptionDuringPerformFunctions() {
    return exceptionsEncounteredDuringPerformPhase;
  }
}
