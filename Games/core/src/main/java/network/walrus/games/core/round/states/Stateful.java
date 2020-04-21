package network.walrus.games.core.round.states;

/**
 * An object which can only be in one {@link RoundState} at a time, and whose state can only go
 * forward in the pre-defined state order as the lifecycle of this object progresses. When created,
 * the object may initially have no state. On the contrary, when the object is finally discarded,
 * the last state should still remain present.
 *
 * @author Austin Mayes
 */
public interface Stateful {

  /**
   * Update the current {@link RoundState} of this object, and return the previous state before the
   * update.
   *
   * @param state new state to set the object to
   * @return old state of the object, if there was one
   */
  RoundState setState(RoundState state);

  /** @return current state of this object */
  RoundState getState();
}
