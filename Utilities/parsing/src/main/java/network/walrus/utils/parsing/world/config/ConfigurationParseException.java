package network.walrus.utils.parsing.world.config;

/**
 * Exception wrapper that is thrown when a configured world fails to parse.
 *
 * @author Avicus Network
 */
public class ConfigurationParseException extends Exception {

  /**
   * Constructor.
   *
   * @param message explaining why parsing failed
   * @param cause of the base exception
   */
  public ConfigurationParseException(String message, Exception cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message explaining why parsing failed
   */
  public ConfigurationParseException(String message) {
    super(message);
  }
}
