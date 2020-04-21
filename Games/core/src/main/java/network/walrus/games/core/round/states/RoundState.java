package network.walrus.games.core.round.states;

/**
 * Represents the base collection of states a {@link Stateful} can be in at any time. Round states
 * all wrap a {@link BaseState} so that game implementations can add in their own custom states,
 * such as death match, without breaking the internal state system. Each state is linked to a
 * pre-defined base state which is used for core logic such as would loading and listener
 * registration.
 *
 * @author Austin Mayes
 */
public interface RoundState {

  RoundState IDLE = () -> BaseState.IDLE;
  RoundState STARTING = () -> BaseState.STARTING;
  RoundState PLAYING = () -> BaseState.PLAYING;
  RoundState FINISHED = () -> BaseState.FINISHED;

  /** @return base state which he parent state is a wrapper for */
  BaseState state();

  /** @return if the state is happening while a game is in progress */
  default boolean playing() {
    return state() == BaseState.PLAYING;
  }

  /** @return if the round has started when this state is in use */
  default boolean started() {
    return state() == BaseState.PLAYING || state() == BaseState.FINISHED;
  }

  /** @return if the round has finished when this state is in use */
  default boolean finished() {
    return state() == BaseState.FINISHED;
  }

  /** @return if the round is starting when this state is in use */
  default boolean starting() {
    return state() == BaseState.STARTING;
  }

  /** Base state relating to the actual state which is wrapping it. */
  enum BaseState {
    IDLE,
    STARTING,
    PLAYING,
    FINISHED
  }
}
