package org.jceremony.model;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class LifecycleModel {
  DefaultDirectedGraph<LifecycleState, DefaultEdge> graph=new DefaultDirectedGraph<>(DefaultEdge.class);
  
  public Map<StateName, LifecycleState> getStates() {
    Map<StateName, LifecycleState> mappedStates = graph.vertexSet().stream().collect(Collectors.toMap(sm->sm.getName(), sm->sm));
    return mappedStates;
  }

  public Graph<LifecycleState, DefaultEdge> getGraph() {
    return this.graph;
  }

  public LifecycleState lookupStateFromLifecycleOrThrowIfNotExists(StateName stateName) {
    Objects.requireNonNull(stateName, "The stateName cannot be null");
    
    LifecycleState state = getStates().get(stateName);
    if(state==null){
      String msg="The state '"+stateName+"' could not be found in the lifecycle '"+this+"'";
      throw new NullPointerException(msg);
    }
    return state;
  }

}
