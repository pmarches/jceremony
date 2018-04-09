package org.jceremony.model;

public interface Statusable {
  public StateName getCurrentStateName();
  public void setNewStatus(StateName newStatus);
}