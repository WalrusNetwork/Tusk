package network.walrus.nerve.core.api.exception;

/**
 * An exception thrown by a failed redis action.
 *
 * @author Austin Mayes
 */
public class RedisException extends ApiException {

  private final String channel;
  private final String content;

  /**
   * Constructor.
   *
   * @param message which provides more info about why the exception occurred
   * @param cause of the base error
   * @param channel which the content is being sent to
   * @param content of the message
   */
  public RedisException(String message, Throwable cause, String channel, String content) {
    super(message, cause);
    this.channel = channel;
    this.content = content;
  }

  public String getChannel() {
    return channel;
  }

  public String getContent() {
    return content;
  }
}
