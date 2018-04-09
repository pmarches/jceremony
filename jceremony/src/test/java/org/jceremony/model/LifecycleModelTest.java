package org.jceremony.model;

import static org.junit.Assert.assertEquals;

import org.hamcrest.core.StringStartsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LifecycleModelTest {
  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Test
  public void testStraightLifeCycle() throws Exception{
    LifecycleModel straightLifeCycle=WellKnownLifecycles.straightLineLifecycle().build();
    assertEquals(6, straightLifeCycle.getStates().size()); //This includes the state init/intial/epoch/juncture/./
//    assertEquals(99, straightLifeCycle.getFunctions().size()); //Separate for checks,routing,commit?
  }

  @Test
  public void testStateHasNoIncomingTransition() throws Exception{
    thrown.expect(Exception.class);
    thrown.expectMessage("The graph is not weakly connected, the states [state1,state2] need to be connected to the 'state0' state");
    new LifecycleModelBuilder().state("state0").state("state1").state("state2").build();
  }

  @Test
  public void testNoDuplicateTransitionBetweenSameStates() throws Exception{
    thrown.expect(Exception.class);
    thrown.expectMessage("The state 'state2' already has a transition from state 'state1'");
    new LifecycleModelBuilder().state("state1").to("state2")
      .state("state1").to("state2")
      .build();
  }
  
  @Test
  public void testInvalidStateNameThrowsException() throws Exception {
    thrown.expect(Exception.class);
    thrown.expectMessage(StringStartsWith.startsWith("The state 'Invalid state ref' could not be found in the lifecycle"));
    WellKnownLifecycles.abcdLifecycle().build().lookupStateFromLifecycleOrThrowIfNotExists(new StateName("Invalid state ref"));
  }

  @Test
  public void testTransitionLoopIsAllowed() throws Exception{
    WellKnownLifecycles.lifecycleWithALoop().build();
  }
  
  @Test
  public void testApplicationLifecyleModel() {
    WellKnownLifecycles.sampleApplicationProcess();
  }
}
