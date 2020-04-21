package network.walrus.utils.core.registry;

/**
 * Exception thrown when something goes wrong in a {@link Registry}.
 *
 * @author Avicus Network
 */
public class RegistryException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param msg of the exception
   */
  public RegistryException(String msg) {
    super(msg);
  }
}
