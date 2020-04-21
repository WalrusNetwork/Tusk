package network.walrus.nerve.bukkit.user;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import network.walrus.nerve.bukkit.NerveBukkitPlugin;
import network.walrus.nerve.bukkit.event.PermissionsLoadedEvent;
import network.walrus.utils.bukkit.listener.EventUtil;
import network.walrus.utils.core.util.PermissionsUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Class which handles attaching permissions from the API to a user.
 *
 * @author Rafi Baum
 */
public class BukkitPermissionHandler implements Listener {

  private final Map<UUID, PermissionAttachment> attachments;

  /** Constructor. */
  public BukkitPermissionHandler() {
    attachments = Maps.newHashMap();
  }

  /**
   * Attaches list of permissions to the specified player.
   *
   * @param player to attach permissions to
   * @param permissions to attach
   */
  public void attachPerms(Player player, List<String> permissions) {
    PermissionAttachment attachment = player.addAttachment(NerveBukkitPlugin.instance());

    for (String permission : permissions) {
      Pair<String, Boolean> decoded = PermissionsUtils.decodePermission(permission);
      attachment.setPermission(decoded.getKey(), decoded.getValue());
    }

    attachments.put(player.getUniqueId(), attachment);
    EventUtil.call(new PermissionsLoadedEvent(player));
  }

  /**
   * If the specified player has permissions from Walrus loaded.
   *
   * @param player to check
   * @return true if permissions from API are attached
   */
  public boolean hasPermsLoaded(Player player) {
    return hasPermsLoaded(player.getUniqueId());
  }

  /**
   * If the specified player has permissions from Walrus loaded.
   *
   * @param uuid of player to check
   * @return true if permissions from API are attached
   */
  public boolean hasPermsLoaded(UUID uuid) {
    return attachments.containsKey(uuid);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    PermissionAttachment attachment = attachments.remove(event.getPlayer().getUniqueId());
    if (attachment != null) {
      event.getPlayer().removeAttachment(attachment);
    }
  }
}
