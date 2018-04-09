package org.jceremony.model;

public class StateName {
  private String stateName;

  public StateName(String stateName) {
    this.stateName=stateName;
  }
  
  @Override
  public String toString() {
    return stateName;
  }
  
  @Override
  public int hashCode() {
    return stateName.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof StateName) {
      return stateName.equals(((StateName) obj).stateName);
    }
    return false;
  }
}
