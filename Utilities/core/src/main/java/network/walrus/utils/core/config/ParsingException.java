package network.walrus.utils.core.config;

import javax.annotation.Nonnull;

/**
 * An exception thrown when a parsable object fails to find data/receives incorrect data.
 *
 * <p>The message for these exceptions should be especially descriptive, since they are sent to the
 * makers of the map/people with permission to view them.
 *
 * @author Avicus Network
 */
public class ParsingException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param node which contains the error
   * @param message to be shown to the user (should be user-friendly)
   */
  public ParsingException(@Nonnull Node node, @Nonnull String message) {
    super(generateMessage(node, message));
  }

  /**
   * Constructor.
   *
   * @param message to be shown to the user (should be user-friendly)
   */
  public ParsingException(@Nonnull String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message to be shown to the user (should be user-friendly)
   * @param cause of the base exception
   */
  public ParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param node which contains the error
   * @param cause of the base exception
   */
  public ParsingException(@Nonnull Node node, @Nonnull Throwable cause) {
    this(node, cause.getMessage());
  }

  private static String generateMessage(@Nonnull Node node, String message) {
    String builder =
        "Unable to parse <"
            + node.name()
            + "/> (line #"
            + node.startLine()
            + " - "
            + node.endLine()
            + "): "
            + message;
    return builder;
  }
}
