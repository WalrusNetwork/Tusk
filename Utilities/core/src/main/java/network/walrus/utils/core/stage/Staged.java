package network.walrus.utils.core.stage;

/**
 * Something that should only be allowed to happen if the server is in a certain {@link Stage}.
 *
 * @author Austin Mayes
 */
public interface Staged {

  /** The current stage of this object. */
  Stage stage();

  /** The current stage of tbe environment containing this object. */
  Stage environment();

  /** If this object should be used based on the current server stage. */
  default boolean usable() {
    return environment() == Stage.DEVELOPMENT
        || environment() == stage(); // Development servers can use everything
  }
}
