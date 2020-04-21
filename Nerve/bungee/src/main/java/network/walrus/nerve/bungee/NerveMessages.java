package network.walrus.nerve.bungee;

import java.util.Arrays;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.MessageReferenceHolder;

/**
 * Translations for messages shown by the nerve plugin.
 *
 * @author Austin Mayes
 */
public class NerveMessages extends MessageReferenceHolder {

  public static final LocalizedFormat ERROR_WHISPER_NOT_FOUND = get("error.whisper.not-found");
  public static final LocalizedFormat ERROR_WHISPER_REPLY_CONSOLE =
      get("error.whisper.reply.console");
  public static final LocalizedFormat ERROR_WHISPER_REPLY_NONE = get("error.whisper.reply.none");
  public static final LocalizedFormat SERVER_LIST_HEADER = get("server.list.header");
  public static final LocalizedFormat ERRORS_INVALID_SERVER = get("error.invalid-server");
  public static final LocalizedFormat ERROR_ALREADY_ON_SERVER = get("error.already-on-server");
  public static final LocalizedFormat ERROR_CANNOT_REQUEST_SERVER =
      get("error.cannot-request-server");
  public static final LocalizedFormat REQUESTED_SERVER_CREATED = get("server.request.created");
  public static final LocalizedFormat SERVER_TYPE_NOT_FOUND = get("error.server-type-not-found");
  public static final LocalizedFormat SERVER_REQUEST_NO_PERMS =
      get("error.server-request-no-perms");
  public static final LocalizedFormat SERVER_REQUEST_CREATED = get("server.request.created");
  public static final LocalizedFormat CURRENT_SERVER = get("server.current");
  public static final LocalizedFormat SERVER_JOIN_FAIL_INTERNAL =
      get("errors.server-join-internal");
  public static final LocalizedFormat SERVER_ALREADY_EXISTS = get("server.request.exists");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(
                ERROR_WHISPER_NOT_FOUND,
                "Message shown to a user when they try to message someone they can't"),
            new MessageInformation(
                ERROR_WHISPER_REPLY_CONSOLE, "Message shown to console when it tries to use /r"),
            new MessageInformation(
                ERROR_WHISPER_REPLY_NONE,
                "Message shown to a user when they try to /r when they haven't received a message"),
            new MessageInformation(SERVER_LIST_HEADER, "The header of the server list command")
                .argument("current page")
                .argument("total pages"),
            new MessageInformation(
                ERRORS_INVALID_SERVER,
                "Error sent when a player tries to join a server which doesn't exist"),
            new MessageInformation(
                ERROR_ALREADY_ON_SERVER,
                "Error sent when a player is already on the server they're trying to join"),
            new MessageInformation(
                ERROR_CANNOT_REQUEST_SERVER,
                "Error sent when the API cannot create the requested server"),
            new MessageInformation(
                    REQUESTED_SERVER_CREATED,
                    "Message shown when a server someone has requested has been created, allowing them to click on the server name to go there")
                .argument("server name"),
            new MessageInformation(
                SERVER_TYPE_NOT_FOUND, "Error sent when the requested server type does not exist"),
            new MessageInformation(
                SERVER_REQUEST_NO_PERMS,
                "Error sent when you don't have permission to open a server"),
            new MessageInformation(
                SERVER_REQUEST_CREATED, "Message sent when a server is being created"),
            new MessageInformation(
                    CURRENT_SERVER, "Message shown telling the player what server they're on")
                .argument("current server name"),
            new MessageInformation(
                SERVER_JOIN_FAIL_INTERNAL,
                "Error shown when a player can't join a server due to an internal failure"),
            new MessageInformation(
                SERVER_ALREADY_EXISTS,
                "Message shown when a player has requested a server which already exists, and is now being sent to it")));
  }
}
