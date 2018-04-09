package org.jceremony.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class LifecycleModelBuilder {
  /*
   * functions: 
   * - ranking function : ranking is when you have multiple functions you need to run in a certain order.
   * 
   * global and specific to a state or transition
   *  These checks should be quick, and leave no side effect. Might be called in parallel. If the check is long running, only the cached result should be checked
   * - global/local check upon the state we are leaving
   * - global/local check before the transition start
   * - global/local check on each target state
   * 
   * - global/local perform upon departure from the source state
   * - global/local perform upon each transition trigger
   * - global/local perform upon arrival to the target state
   * 
   * Timeline: The globals are mixed with the locals and ranked.
   * Source state (SS) : Check source state can be left
   * Transition   (TR) : Check transition can be activated
   * Target State (TS) : Check target state can be reached
   *               SS  : Prepare state departure
   *               TS  : Prepare state arrival
   *               SS  : Perform after state departed
   *               TR  : Perform transition
   *               TS  : Perform after state arrived
   * 
   * Error handling:
   * - Checks should not throw errors, simply return false
   * - Errors during the perform phase can either:
   *    - Stop the transition
   *    - Continue to the target state
   *    - Go to different state?
   *    - Remain stuck in the transition? !!!!!!!!!!!1Thus a transition is a state!!!!!!!!!!!
   * 
   * Transitions as state: They make sense because while the transition is occuring what is the state of the object?
   * So transitions exists only as states between two states. They cannot be initial or final state. Transitions also have a
   * "onError" transition that triggers upon processing error.
   * The init state is actually a transition without a source state.
   * 
   * What is different between a resting state and a transitive state?
   * - Transitive states are expected to remain in that state for shorter periods of time, could still be months/years
   * - Transitive states are expected to leave state without an external trigger. They should self trigger upon certain conditions.
   * - They require a source state and a target state
   * - So all differences are only because of the state configuration. 
   * 
   * scenario: Source state linked by transition to target state. Start with source state and transition as the target state
   * (SS->TR then TR->TS)
   * SS : Check state can depart
   * TS : Check target state can arrive
   * SS : State departed
   * TS : State arrived. This can be async and last for a long time!
   * Repeat to reach the target state
   * 
   * 
   * Upon object modification
   * - global/local check function triggered before an object is modified (can veto a change)
   * - global/local perform function triggered after an object is modified
   */
  HashMap<StateName, StateModelBuilder> stateBuilders=new HashMap<>();
//  HashMap<String, RankNextTransition<Object>> routingFunctions=new HashMap<>();
//  CheckStateChangeFactory checkStateChangedFactory; //Higher order function
  StateName nameOfFirstState;

  public class StateModelBuilder {
    StateName stateName;
    List<StateName> sourceStateNames=new ArrayList<>(); //FIXME SHould this list be changed to a Set?
    List<String> checkIfThisStateCanBeDeparted=new ArrayList<>();
    List<String> checkIfThisStateCanBeArrived=new ArrayList<>();
    List<String> uponDepartedThisState=new ArrayList<>();
    List<String> uponArrivedToThisState=new ArrayList<>();

    public StateModelBuilder(StateName stateName) {
      this.stateName=stateName;
    }

    public StateModelBuilder state(String stateName) {
      return state(new StateName(stateName));
    }
    public StateModelBuilder state(StateName stateName) {
      return LifecycleModelBuilder.this.state(stateName);
    }

    LifecycleState toStateModel(){
      LifecycleState newStateModel=new LifecycleState(stateName);
      newStateModel.checkIfThisStateCanBeDeparted=Collections.unmodifiableList(checkIfThisStateCanBeDeparted);
      newStateModel.checkIfThisStateCanBeArrived=Collections.unmodifiableList(checkIfThisStateCanBeArrived);
      newStateModel.uponDepartedFromThisState=Collections.unmodifiableList(uponDepartedThisState);
      newStateModel.uponArrivedToThisState=Collections.unmodifiableList(uponArrivedToThisState);
      return newStateModel;
    }

    public LifecycleModel build() throws Exception {
      return LifecycleModelBuilder.this.build();
    }

    public LifecycleModelBuilder builder() {
      return LifecycleModelBuilder.this;
    }

    public StateModelBuilder addUponArrival(String functionName) {
      uponArrivedToThisState.add(functionName);
      return this;
    }

    public StateModelBuilder addUponDeparture(String functionName) {
      uponDepartedThisState.add(functionName);
      return this;
    }

    public StateModelBuilder addCheckIfCanDepart(String functionName) {
      checkIfThisStateCanBeDeparted.add(functionName);
      return this;
    }

    public StateModelBuilder addCheckIfCanArrive(String functionName) {
      checkIfThisStateCanBeArrived.add(functionName);
      return this;
    }

    public StateModelBuilder to(String targetStateName) throws RuntimeException {
      return to(new StateName(targetStateName));
    }
    
    public StateModelBuilder to(StateName targetStateName) throws RuntimeException {
      StateModelBuilder targetStateBuilder=LifecycleModelBuilder.this.state(targetStateName);
      targetStateBuilder.from(this.stateName);
      return this;
    }

    public StateModelBuilder from(String sourceStateName) {
      return from(new StateName(sourceStateName));
    }
    public StateModelBuilder from(StateName sourceStateName) {
      if(sourceStateNames.contains(sourceStateName)) {
        String msg=String.format("The state '%s' already has a transition from state '%s'", this.stateName, sourceStateName);
        throw new RuntimeException(msg);
      }
      sourceStateNames.add(sourceStateName);
      return LifecycleModelBuilder.this.state(sourceStateName);
    }

    public StateModelBuilder toState(String nextStateName) {
      return toState(new StateName(nextStateName));
    }
    public StateModelBuilder toState(StateName nextStateName) {
      StateModelBuilder nextState = state(nextStateName);
      nextState.from(stateName);
      return nextState;
    }
}
  
  public LifecycleModelBuilder(){
  }
  
  public LifecycleModel build() throws Exception {
    LifecycleModel modelToReturn=new LifecycleModel();
    Map<StateName, LifecycleState> statesByName = stateBuilders.values().stream().map(StateModelBuilder::toStateModel)
        .collect(Collectors.toMap(sm->sm.getName(), sm->sm));
    statesByName.values().stream().forEach(modelToReturn.graph::addVertex);

    for(StateModelBuilder stateModelBuilder : stateBuilders.values()) {
      LifecycleState thisState=statesByName.get(stateModelBuilder.stateName);
      for(StateName sourceStateName : stateModelBuilder.sourceStateNames){
        LifecycleState sourceState=statesByName.get(sourceStateName);
        if(sourceState==null){
          throw new Exception("In state '"+stateModelBuilder.stateName+"', the source state '"+sourceStateName+"' does not exist");
        }
        modelToReturn.graph.addEdge(sourceState, thisState);
      }

    }
    LifecycleState firstState = statesByName.get(nameOfFirstState);
    throwIfAllVerticesAreNotFullyConnectedToThisState(modelToReturn.graph, firstState);
    return modelToReturn;
  }

  private void throwIfAllVerticesAreNotFullyConnectedToThisState(DefaultDirectedGraph<LifecycleState, DefaultEdge> graph, LifecycleState firstState) throws Exception {
    Objects.requireNonNull(graph);
    Objects.requireNonNull(firstState);
    
    ConnectivityInspector<LifecycleState, DefaultEdge> inspector=new ConnectivityInspector<>(graph);
    if(inspector.isGraphConnected()==false){
      ArrayList<LifecycleState> allStatesNotWeaklyConnectedToInit=new ArrayList<>(graph.vertexSet());
      allStatesNotWeaklyConnectedToInit.removeAll(inspector.connectedSetOf(firstState));
      String exceptionMsg=String.format("The graph is not weakly connected, the states [%s] need to be connected to the '%s' state",
          allStatesNotWeaklyConnectedToInit.stream()
            .sorted((l,r)->l.stateName.toString().compareTo(r.stateName.toString())) //Sorted for the unit tests
            .map(l->l.getName().toString())
            .collect(Collectors.joining(",")),
          nameOfFirstState);
      throw new Exception(exceptionMsg);
    }
  }

  public StateModelBuilder state(String stateName) {
    return state(new StateName(stateName));
  }
  
  public StateModelBuilder state(StateName stateName) {
    StateModelBuilder namedBuilder=stateBuilders.get(stateName);
    if(namedBuilder==null){
      namedBuilder=new StateModelBuilder(stateName);
      stateBuilders.put(stateName, namedBuilder);
      if(nameOfFirstState==null) {
        nameOfFirstState=stateName;
      }
    }
    return namedBuilder;
  }
}
