package network.walrus.ubiquitous.bukkit.compat;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import us.myles.ViaVersion.api.Via;

/**
 * Holds instances of classes which are used to facilitate fallback behaviour for players using
 * legacy (<1.8) clients.
 *
 * @author Rafi Baum
 */
public class CompatManager implements Listener {

  private final Map<UUID, Integer> playerApiVersion = Maps.newHashMap();
  private final CompatTitleScreen compatTitleScreen;
  private final CompatActionBar compatActionBar;
  private final CompatEnchantingTable compatEnchantingTable;

  public CompatManager() {
    compatTitleScreen = new CompatTitleScreen(this);
    compatActionBar = new CompatActionBar(this);
    compatEnchantingTable = new CompatEnchantingTable(this);
    Bukkit.getPluginManager()
        .registerEvents(compatEnchantingTable, UbiquitousBukkitPlugin.getInstance());
  }

  /**
   * @param uuid of the player to check
   * @return the protocol version of the specified player
   */
  public int getVersion(UUID uuid) {
    return playerApiVersion.computeIfAbsent(uuid, (cUuid -> Via.getAPI().getPlayerVersion(cUuid)));
  }

  /**
   * @param player to check
   * @return the protocol version of the specified player
   */
  public int getVersion(Player player) {
    return getVersion(player.getUniqueId());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    playerApiVersion.remove(event.getPlayer().getUniqueId());
  }

  /** @return compatibility wrapper for title screens */
  public CompatTitleScreen getCompatTitleScreen() {
    return compatTitleScreen;
  }

  /** @return compatibility wrapper for action bars */
  public CompatActionBar getCompatActionBar() {
    return compatActionBar;
  }
}
