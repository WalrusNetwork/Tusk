package network.walrus.games.core.facets.group.spectate;

import java.util.Collections;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener responsible for giving out the default spectator items. It is up to specific game
 * implementations to register the listener themselves, since it is not enabled by default. The only
 * negative effect of not registering this listener will be that players will be given no items
 * while spectating. Permissions are handled in the {@link ObserverListener}.
 *
 * @author Austin Mayes
 */
public class SpectatorListener extends FacetListener<GroupsManager> {

  /**
   * @param holder this listener is a part of
   * @param facet used for spectator comparison
   */
  public SpectatorListener(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
  }

  private ItemStack createTeleportDevice(Player player) {
    ItemStack stack = new ItemStack(Material.COMPASS);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(
        GamesCoreMessages.UI_TELEPORT_DEVICE_NAME
            .with(Games.Items.COMPASS_NAME)
            .render(player)
            .toLegacyText());
    meta.setLore(
        Collections.singletonList(
            GamesCoreMessages.UI_TELEPORT_DEVICE_DESCRIPTION
                .with(Games.Items.COMPASS_LORE)
                .render(player)
                .toLegacyText()));

    stack.setItemMeta(meta);
    return stack;
  }

  /** Give out spectator items. */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    if (!this.getFacet().getSpectators().equals(event.getGroup())) {
      return;
    }

    if (!event.isGiveKit()) {
      return;
    }

    Player player = event.getPlayer();

    int slot;
    ItemStack item = player.getInventory().getItem(0);
    if (isEmpty(item)) {
      slot = 0;
    } else {
      slot = 1;
      while (!isEmpty(player.getInventory().getItem(slot)) && slot < 7) {
        slot++;
      }

      slot++;
    }

    player.getInventory().setItem(slot, createTeleportDevice(event.getPlayer()));
  }

  private boolean isEmpty(ItemStack item) {
    return item == null || item.getType() == Material.AIR;
  }
}
