package org.jceremony.model;

import java.util.List;

public class LifecycleState {
  protected StateName stateName;
  
  public List<String> checkIfThisStateCanBeDeparted;
  public List<String> checkIfThisStateCanBeArrived;
  public List<String> uponDepartedFromThisState;
  public List<String> uponArrivedToThisState;

  public LifecycleState(StateName stateName){
    this.stateName=stateName;
  }
  
  public StateName getName(){
    return stateName;
  }
}
