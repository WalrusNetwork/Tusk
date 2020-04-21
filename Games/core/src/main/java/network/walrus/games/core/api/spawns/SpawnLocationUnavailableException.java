package network.walrus.games.core.api.spawns;

/**
 * Exception wrapper that is thrown when a spawn runs out of positions to generate.
 *
 * @author Austin Mayes
 */
public class SpawnLocationUnavailableException extends Exception {

  /** Constructor. */
  public SpawnLocationUnavailableException(String message) {
    super(message);
  }
}
