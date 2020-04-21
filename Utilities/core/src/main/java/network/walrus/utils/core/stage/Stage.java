package network.walrus.utils.core.stage;

/**
 * Represents the stage that this server environment is currently running in, This is used to
 * determine which {@link Staged} objects should and can be used for certain purposes.
 *
 * @author Austin Mayes
 */
public enum Stage {
  DEVELOPMENT,
  TOURNAMENT,
  PRODUCTION
}
