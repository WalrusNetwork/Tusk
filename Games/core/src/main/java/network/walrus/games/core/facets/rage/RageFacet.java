package network.walrus.games.core.facets.rage;

import java.util.Optional;
import network.walrus.games.core.GamesPlugin;
import network.walrus.utils.parsing.facet.Facet;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Controls rage mode
 *
 * @author Wesley Smith
 */
public class RageFacet extends Facet implements Listener {

  private String metadataKey = "ragearrow";
  private FixedMetadataValue metadataValue = new FixedMetadataValue(GamesPlugin.instance, true);

  /** Apply rage */
  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      boolean kill = false;
      if (event.getDamager() instanceof Arrow) {
        kill = event.getDamager().hasMetadata(metadataKey);
      } else if (event.getDamager() instanceof Player) {
        Player damager = (Player) event.getDamager();
        kill =
            Optional.ofNullable(damager.getItemInHand())
                    .map(stack -> stack.getEnchantmentLevel(Enchantment.DAMAGE_ALL))
                    .orElse(0)
                > 1;
      }

      if (kill) {
        event.setDamage(1000);
      }
    }
  }

  /** Add rage data to arrows */
  @EventHandler
  public void onShoot(EntityShootBowEvent event) {
    if (event.getActor() instanceof Player
        && event.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) > 1) {
      event.getProjectile().setMetadata(metadataKey, metadataValue);
    }
  }
}
