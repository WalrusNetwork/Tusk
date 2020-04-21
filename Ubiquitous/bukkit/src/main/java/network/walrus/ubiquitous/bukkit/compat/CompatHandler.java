package network.walrus.ubiquitous.bukkit.compat;

import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * Wrapper class containing common functionality for compatibility handlers.
 *
 * @author Rafi Baum
 */
public abstract class CompatHandler {

  private final CompatManager compatManager;

  /**
   * Constructor.
   *
   * @param compatManager
   */
  public CompatHandler(CompatManager compatManager) {
    this.compatManager = compatManager;
  }

  /**
   * Checks if a connecting player is a legacy client.
   *
   * @param uuid to check
   * @return if the player is a legacy client
   */
  public boolean isLegacy(UUID uuid) {
    return compatManager.getVersion(uuid) < 47;
  }

  /**
   * Checks if a connecting player is a legacy client.
   *
   * @param player to check
   * @return if the player is a legacy client
   */
  public boolean isLegacy(Player player) {
    return isLegacy(player.getUniqueId());
  }

  /**
   * Checks if a player is a legacy client and throws an exception otherwise.
   *
   * @param player to check
   */
  public void requireSupport(Player player) {
    if (isLegacy(player)) {
      throw new IllegalStateException("Attempted to use modern feature with legacy client!");
    }
  }
}
