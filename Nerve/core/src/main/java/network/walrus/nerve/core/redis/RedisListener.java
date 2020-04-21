package network.walrus.nerve.core.redis;

/**
 * Object which receives push updates from Redis when data is sent to any of the specified {@link
 * #channels()}.
 *
 * @param <D> type of document the listener is responsible for
 * @author Austin Mayes
 */
public interface RedisListener<D> {

  /**
   * Called when a message is sent to any of the subscribed channels.
   *
   * <p>This method is *NOT* called on the main thread.
   *
   * @param document received from Redis
   * @throws Exception if handling fails for any reason
   */
  void handle(D document) throws Exception;

  /** @return array of channels that this listener should listen for messages on */
  String[] channels();

  /** @return type pf class this listener expects to receive */
  Class<D> type();

  /**
   * Determine if this listener should handle a message sent to a specific channel.
   *
   * @param channel to check
   * @return if the listener should handle the message
   */
  default boolean matches(String channel) {
    for (String check : channels()) {
      if (check.equals(channel)) {
        return true;
      }
    }
    return false;
  }
}
