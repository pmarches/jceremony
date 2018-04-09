What is this?
A Library for managing the lifecycle of a long lived object going thru long running processes.

Reqs:
- A single object may have multiple independent statuses linked to a single or multiple lifecycles. 
- State changes can occur months or years appart. Needs to support reloading the execution context across process restart, object creation/destruction.
- Support for optional automatic promotion path decision.

API:
- Would be nice to have a programatically defined state per lifecycle. 

Versioning:
- Support versionning of the lifecycle. Need to augment the model with transition from an old state in a previous version of the lifecyle to a new state of the current lifecycle. Need to serialize the lifecycle and it's old versions in a file (SQLite? JSON?) The idea for the model to handle multiple versions, is to manage the migrations from an older version to a newer version. Does this require the states to become a UUID instead of labels?
  - in Java, versionning is done at the jar level, so we need some sort of mechanism for mapping old state names (which may or may not exist in the newer lifecycle) and map them to the latest state. This is a regular transition? Or maybe, the old versions should be kept in the lifecycle?

Batching:
- Support for batching large amount of statuses. Should be able to be fed with the status object and the primary key (which may be composite). 