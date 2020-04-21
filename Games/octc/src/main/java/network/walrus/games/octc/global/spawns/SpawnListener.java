package network.walrus.games.octc.global.spawns;

import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.bukkit.NMSUtils;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Listener responsible for passing all player and round lifecycle events to the {@link
 * SpawnsManager}.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class SpawnListener extends FacetListener<OCNSpawnManager> {

  private final GroupsManager groupsManager;

  public SpawnListener(FacetHolder holder, OCNSpawnManager module) {
    super(holder, module);
    this.groupsManager = holder.getFacetRequired(GroupsManager.class);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinTeam(PlayerChangedGroupEvent event) {
    event.yield();
    getFacet().stopRespawnTask(event.getPlayer(), false);

    if (event.isSpawnTriggered()) {
      getFacet().spawn(event.getGroup(), event.getPlayer(), true, event.isTeleportTriggered());
    }

    getFacet().setDead(event.getPlayer(), false);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    this.getFacet().stopRespawnTask(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!this.getFacet().isRespawning(event.getPlayer())) {
      return;
    }

    if (event.getAction() != Action.LEFT_CLICK_AIR
        && event.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }

    this.getFacet().queueAutoRespawn(event.getPlayer());
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerInteractEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player damager = (Player) event.getDamager();

    if (!this.getFacet().isRespawning(damager)) {
      return;
    }

    this.getFacet().queueAutoRespawn(damager);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {
    Player player = event.getPlayer();
    PlayerStartRespawnEvent respawnEvent = new PlayerStartRespawnEvent(player);

    PlayerUtils.reset(player);

    if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      NMSUtils.playDeathAnimation(
          player); // Have to do this because it doesn't play when we set health to 20.
    }

    EventUtil.call(respawnEvent);

    if (respawnEvent.isCanceled()) return;

    this.getFacet().setDead(player, true);
    getHolder().getFacetRequired(GroupsManager.class).refreshObserver(player);
    this.getFacet().startRespawnTask(player);
  }
}
