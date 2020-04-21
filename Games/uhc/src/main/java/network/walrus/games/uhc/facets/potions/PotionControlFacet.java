package network.walrus.games.uhc.facets.potions;

import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionEffectAddEvent.EffectAddReason;
import org.bukkit.potion.PotionEffectType;

/**
 * Facet which controls potion effects based off of the UHC config.
 *
 * @author Austin Mayes
 */
public class PotionControlFacet extends Facet implements Listener {

  private final UHCConfig config;
  private final FacetHolder holder;

  /** @param holder which this facet is operating inside of */
  public PotionControlFacet(FacetHolder holder) {
    this.holder = holder;
    this.config = UHCManager.instance.getConfig();
  }

  /** Cancel effects if they violate the config */
  @EventHandler
  public void onEffectAdd(PotionEffectAddEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();

    if (!holder.getContainer().players().contains(player)) {
      return;
    }

    if (event.getReason() == EffectAddReason.FOOD) {
      if (event.getEffect().getType().equals(PotionEffectType.ABSORPTION)) {
        if (!config.absorption.get()) {
          event.setCancelled(true);
        }
      }

      return;
    }

    if (!(event.getReason().equals(EffectAddReason.POTION_DRINK)
        || event.getReason().equals(EffectAddReason.POTION_SPLASH))) {
      return;
    }

    if (!config.potions.get()) {
      event.setCancelled(true);
      player.sendMessage(
          UHCMessages.prefix(
              UHCMessages.ERROR_POTIONS_DISABLED_ALL.with(
                  NetworkColorConstants.Games.UHC.Potions.DISABLED)));
    } else if (!config.strengthTwo.get()
        && event.getEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)
        && event.getEffect().getAmplifier() >= 2) {
      event.setCancelled(true);
      player.sendMessage(
          UHCMessages.prefix(
              UHCMessages.ERROR_POTIONS_DISABLED_STRENGTH.with(
                  NetworkColorConstants.Games.UHC.Potions.DISABLED)));
    }
  }
}
