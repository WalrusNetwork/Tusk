package network.walrus.games.uhc.scenarios.type;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByPlayerEvent;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scenarios.NoClean;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.MaterialData;

/**
 * Scenario which gives players invulnerability for a short period of time after killing someone.
 *
 * @author Rafi Baum
 */
public class NoCleanScenario extends Scenario {

  private static final int invulnerabilitySeconds = 30;
  private final Map<UUID, GameTask[]> invulnerablePlayers;

  /** Constructor */
  public NoCleanScenario() {
    invulnerablePlayers = Maps.newHashMap();
  }

  @Override
  public String name() {
    return "NoClean";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_NO_CLEAN;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.OBSIDIAN);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return new ScenarioAuthorInfo(
        "/u/NotMyLights", "https://www.reddit.com/r/ultrahardcore/comments/4qg2ie/noclean/");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerKill(PlayerDeathByPlayerEvent event) {
    onKill(event.getCause());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTaggedKill(TaggedPlayerDeathByPlayerEvent event) {
    onKill(event.getCause());
  }

  private void onKill(Player killer) {
    GameTask[] tasks = new GameTask[2];

    tasks[0] =
        GameTask.of(
            "invulnerability-cancel",
            () -> {
              invulnerablePlayers.remove(killer.getUniqueId());
              killer.sendMessage(UHCMessages.NOCLEAN_EXPIRED.with(NoClean.EXPIRED));
            });
    tasks[0].later(invulnerabilitySeconds * 20);

    if (invulnerabilitySeconds >= 10) {
      tasks[1] =
          GameTask.of(
              "invulnerability-warning",
              () ->
                  killer.sendMessage(
                      UHCMessages.NOCLEAN_REMAINING.with(
                          NoClean.REMAINING, new LocalizedNumber(5, NoClean.TIME_LEFT))));
      tasks[1].later(invulnerabilitySeconds * 20 - 5 * 20);
    }

    invulnerablePlayers.put(killer.getUniqueId(), tasks);
    killer.sendMessage(
        UHCMessages.NOCLEAN_GIVEN.with(
            NoClean.GAINED, new LocalizedNumber(invulnerabilitySeconds, NoClean.TIME_LEFT)));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamagePlayer(PlayerDamageEvent event) {
    if (!(event.getInfo().getResolvedDamager() instanceof Player)) return;

    Player damager = (Player) event.getInfo().getResolvedDamager();
    if (!invulnerablePlayers.containsKey(damager.getUniqueId())) return;

    // Player loses invulnerability
    cancelTasks(damager.getUniqueId());
    damager.sendMessage(UHCMessages.NOCLEAN_LOST.with(NoClean.LOST));
  }

  @EventHandler
  public void onPlayerDamaged(PlayerDamageEvent event) {
    if (!invulnerablePlayers.containsKey(event.getEntity().getUniqueId())) return;
    // Player is invulnerable, cancel damage done by other players
    LivingEntity damager = event.getInfo().getResolvedDamager();
    if (damager == null || damager.getUniqueId().equals(event.getEntity().getUniqueId())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    cancelTasks(event.getPlayer().getUniqueId());
  }

  private void cancelTasks(UUID uuid) {
    GameTask[] tasks = invulnerablePlayers.get(uuid);
    if (tasks == null) return;

    tasks[0].cancel();
    if (tasks[1] != null) tasks[1].cancel();
    invulnerablePlayers.remove(uuid);
  }
}
