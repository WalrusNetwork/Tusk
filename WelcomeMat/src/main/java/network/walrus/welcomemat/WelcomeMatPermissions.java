package network.walrus.welcomemat;

/**
 * A constants class containing all of the permissions players can have that allow them to perform
 * functions in the welcome mat plugin environment.
 *
 * <p>Permissions should do *one* and *only one* thing per node, and no "group" permission nodes
 * (such as {@code walrus.staff}) should be used. This is so we can fine-tune permissions later at
 * any time, without having to refactor a lot of code.
 *
 * @author Austin Mayes
 */
public class WelcomeMatPermissions {

  /** The permissions which allows users to receive chat alerts about configuration errors. */
  public static final String VIEW_LOBBY_ERRORS = "walrus.lobby.errors.maps";
}
