package network.walrus.games.uhc.facets.healthindicator;

import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.types.SettingTypes;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.info.ProjectileDamageInfo;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Facet which sends health updates to shooting players when they shoot another player with a bow.
 *
 * @author Austin Mayes
 */
public class HealthIndicatorFacet extends Facet implements Listener {

  static final Setting<Boolean> SHOW_HEALTH_SETTING =
      new Setting<>(
          "games.uhc.hit-messages",
          SettingTypes.BOOLEAN,
          true,
          UHCMessages.PROJECTILE_SETTING_NAME.with(),
          UHCMessages.PROJECTILE_SETTING_DESC.with());
  private final FacetHolder holder;
  private GroupsManager manager;

  /** @param holder which this facet is a part of */
  public HealthIndicatorFacet(FacetHolder holder) {
    this.holder = holder;
  }

  @Override
  public void load() throws FacetLoadException {
    this.manager = holder.getFacetRequired(GroupsManager.class);
  }

  /** Send message on shoot */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onHit(PlayerDamageEvent event) {
    handleShot(event);
  }

  /** Send message on shoot */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onHit(TaggedPlayerDamageEvent event) {
    handleShot(event);
  }

  private void handleShot(EntityDamageEvent event) {
    if (!holder.getContainer().isInside(event.getEntity())) return;
    if (!(event.getInfo() instanceof ProjectileDamageInfo)) return;
    LivingEntity damager = event.getInfo().getResolvedDamager();
    if (damager instanceof Player) {
      if (damager == event.getEntity()) return;
      if (PlayerSettings.get((Player) damager, SHOW_HEALTH_SETTING)) {
        GameTask.of(
                "Health display",
                () -> {
                  // If it's not a player, combat loggers are always playing
                  if (!(event.getEntity() instanceof Player)
                      || manager.isObservingOrDead((Player) event.getEntity())) return;
                  event
                      .getInfo()
                      .getResolvedDamager()
                      .sendMessage(
                          UHCMessages.PROJECTILE_HIT.with(
                              NetworkColorConstants.Games.UHC.Projectile.HIT_TEXT,
                              new PersonalizedBukkitPlayer(event.getEntity()),
                              new LocalizedNumber(event.getEntity().getHealth(), 0, 0)));
                })
            .later(5);
      }
    }
  }
}
