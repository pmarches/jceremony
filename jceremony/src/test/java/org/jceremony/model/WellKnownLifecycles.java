package org.jceremony.model;

public class WellKnownLifecycles {
  public static LifecycleModelBuilder abcdLifecycle(){
    LifecycleModelBuilder abcLifeCycle = new LifecycleModelBuilder()
        .state("a") //The first state defined will always be sourced from "__init__"
          .addCheckIfCanArrive("checkArrivalA1") //Function names are scoped to a lifecycle, not just a state. This allows re-using functions
          .addUponArrival("performAfterArrivalA1")
          .addCheckIfCanDepart("checkCanDepartureA")
          .addUponDeparture("performAfterDepartureA")
          .to("b")
        .state("b")
          .addUponArrival("performAfterArrivalB1")
          .addUponArrival("performAfterArrivalB2")
          .addUponArrival("performAfterArrivalB3")
          .addCheckIfCanArrive("checkCanArrivalB1")
          .to("c")
        .state("c")
          .addUponArrival("performAfterArrivalC1")
          .addUponArrival("performAfterArrivalC2")
          .addCheckIfCanDepart("checkCanDepartureC")
          .to("d")
        .state("d")
          .to("z")
        .state("a")
          .addUponArrival("performAfterArrivalA2")
          .to("z")
        .builder();
    return abcLifeCycle;
  }

  public static LifecycleModelBuilder sampleApplicationProcess(){
    LifecycleModelBuilder passportApplicationProcessLifeCycle = new LifecycleModelBuilder()
        .state("applicant can edit")
        .toState("basic application validation by website")
        .toState("correlate applicant's identity from application details")
        .toState("validate applicant's identity")
        .toState("state department security screening") //Should be done in ||
        .toState("interpol security screening")
        .toState("associate passport documents returned by appliant")
        .toState("verify biometric parameters of application photoid")
        .toState("final approval") //At what steps should the document status not drive the process? a generic form editing process can trigger a more specialized passport application process.
        .toState("print out physical passport")
        .toState("mail out physical passport")
        .builder();
    //How to tie other lifecycles with this one?

    LifecycleModelBuilder passportLifeCycle = new LifecycleModelBuilder()
        .state("passport number allocated")
        .toState("associate citizen's identity")
        .toState("photoId page printed")
        .toState("passport number burned")
        .toState("packaged for shipping")
        .toState("shipped")
        .toState("delivered")
        .builder();
    
    return passportApplicationProcessLifeCycle;
  }
  
  public static LifecycleModelBuilder straightLineLifecycle() {
    LifecycleModelBuilder straightLifeCycle = new LifecycleModelBuilder()
        .state("starting")
          .addCheckIfCanArrive("checkIfCanBeStarted")
//          .addBeforeTransitionTriggers("checkIfCanBeStarted") //This will trigger for any incoming transitions
          .to("started")
        .state("started")
          .to("stopping")
        .state("stopping")
          .to("stopped")
        .state("destroying") //Implicit endTransition()?
          .addUponArrival("checkIfCanBeDestroying")
          .addUponArrival("doTheDestroying")
          .to("destroyed")
          .from("stopped")
        .state("stopped") //States can be defined either before or after they are linked to transitions
          .addUponDeparture("checkIfCanBeStopped")
          .addUponDeparture("onWasStopped")
        .builder();
    return straightLifeCycle;
  }
  
  public static LifecycleModelBuilder lifecycleWithALoop(){
    LifecycleModelBuilder loopy = new LifecycleModelBuilder()
        .state("end").to("end")
        .builder();
    return loopy;
  }
  
  public static LifecycleModelBuilder abLifecycle(){
    LifecycleModelBuilder abLifeCycle = new LifecycleModelBuilder()
        .state("a")
          .addCheckIfCanArrive("checkArrivalA1")
          .addUponArrival("performAfterArrivalA1")
          .addCheckIfCanDepart("checkCanDepartureA")
          .addUponDeparture("performAfterDepartureA")
          .to("b")
        .state("b")
          .addUponArrival("performAfterArrivalB1")
          .addCheckIfCanArrive("checkCanArrivalB1")
        .builder();
    return abLifeCycle;
  }

}
