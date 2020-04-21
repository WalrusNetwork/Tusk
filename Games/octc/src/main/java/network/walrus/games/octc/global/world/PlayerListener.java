package network.walrus.games.octc.global.world;

import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Some miscellaneous listeners which make OCN gamemodes work as people expect them to.
 *
 * @author Rafi Baum
 */
public class PlayerListener extends FacetListener<WorldFacet> {

  /** Constructor */
  public PlayerListener(FacetHolder holder, WorldFacet facet) {
    super(holder, facet);
  }

  /** Disable self-harming arrows */
  @EventHandler(priority = EventPriority.HIGH)
  public void onShootShelf(EntityDamageByEntityEvent event) {
    if (event.getDamager().getType() != EntityType.ARROW) {
      return;
    }

    Projectile projectile = (Projectile) event.getDamager();
    if (projectile.getShooter().equals(event.getEntity())) {
      event.setCancelled(true);
    }
  }
}
