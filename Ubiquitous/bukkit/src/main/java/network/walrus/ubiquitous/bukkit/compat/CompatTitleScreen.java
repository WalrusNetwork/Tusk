package network.walrus.ubiquitous.bukkit.compat;

import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Handles backwards compatibility for title screens. We generally expect the developers to handle
 * backwards compatibility themselves as the way you may want to handle it changes depending on how
 * the title screen is being used.
 *
 * @author Rafi Baum
 */
public class CompatTitleScreen extends CompatHandler {

  public CompatTitleScreen(CompatManager compat) {
    super(compat);
  }

  /**
   * Sends a title screen to a player if they support it. Throws an exception otherwise.
   *
   * @param player
   * @param title to show to the player
   */
  public void sendTitle(Player player, Title title) {
    requireSupport(player);
    player.sendTitle(title);
  }

  /**
   * Updates a title screen if the player supports it. Throws an exception otherwise.
   *
   * @param player
   * @param title to show to the player
   */
  public void updateTitle(Player player, Title title) {
    requireSupport(player);
    player.updateTitle(title);
  }

  /**
   * Hides a title screen from a player. Throws an exception otherwise.
   *
   * @param player
   */
  public void hideTitle(Player player) {
    if (isLegacy(player)) {
      return;
    }

    player.hideTitle();
  }
}
