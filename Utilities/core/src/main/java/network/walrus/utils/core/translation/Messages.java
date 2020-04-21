package network.walrus.utils.core.translation;

import java.util.Arrays;
import network.walrus.utils.core.text.LocalizedFormat;

/**
 * Global non plugin-specific translations for the network.
 *
 * @author Avicus Network
 */
public class Messages extends MessageReferenceHolder {

  public static final LocalizedFormat GUI_PAGE_NEXT = get("gui.page.next");
  public static final LocalizedFormat GUI_PAGE_PREV = get("gui.page.prev");
  public static final LocalizedFormat INTERNAL_ERROR = get("error.internal");
  public static final LocalizedFormat ERRORS_INVALID_PAGE = get("error.invalid-page");
  public static final LocalizedFormat RESTART_NOW = get("restart.now");
  public static final LocalizedFormat RESTART_WARNING = get("restart.warning");
  public static final LocalizedFormat RESTART_KICK = get("restart.kick");
  public static final LocalizedFormat RESTART_QUEUE_QUEUED = get("restart.queue.queued");
  public static final LocalizedFormat RESTART_QUEUE_ALREADY = get("restart.queue.already");
  public static final LocalizedFormat RESTART_CANCEL_CANCELED = get("restart.cancel.canceled");
  public static final LocalizedFormat RESTART_CANCEL_NONE = get("restart.cancel.none");
  public static final LocalizedFormat RESTART_FAILED = get("restart.failed.now");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(GUI_PAGE_NEXT, "Next page button"),
            new MessageInformation(GUI_PAGE_PREV, "Previous page button"),
            new MessageInformation(INTERNAL_ERROR, "Description of an unknown error"),
            new MessageInformation(ERRORS_INVALID_PAGE, "error when user supplies an invalid page")
                .argument("maximum allowed pages"),
            new MessageInformation(
                RESTART_NOW,
                "Message sent to users when they tell the server to restart immediately"),
            new MessageInformation(
                RESTART_WARNING,
                "Message sent to online players saying the server will restart soon"),
            new MessageInformation(RESTART_KICK, "Kick message for planned restarts"),
            new MessageInformation(
                RESTART_QUEUE_QUEUED, "Message sent to players when they queue a restart"),
            new MessageInformation(
                    RESTART_QUEUE_ALREADY,
                    "Error sent to players when they queue a restart when one has already been queued")
                .argument("command to cancel a queued restart"),
            new MessageInformation(
                RESTART_CANCEL_CANCELED,
                "Message sent to players when they cancel a queued restart"),
            new MessageInformation(
                    RESTART_CANCEL_NONE,
                    "Error sent to players when they attempt to cancel a restart when none is queued")
                .argument("command to queue a restart"),
            new MessageInformation(
                    RESTART_FAILED,
                    "Error sent to players when they attempt to restart the server and it can't be at this time")
                .argument("fail reason")));
  }
}
