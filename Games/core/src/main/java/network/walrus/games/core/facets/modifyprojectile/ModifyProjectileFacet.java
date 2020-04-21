package network.walrus.games.core.facets.modifyprojectile;

import java.util.Optional;
import java.util.logging.Level;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.core.math.PreparedNumberAction;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Facet which allows for bow projectile entities to be modified with different entity types,
 * velocities, and potion effects.
 *
 * @author Rafi Baum
 */
public class ModifyProjectileFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final Optional<Class<? extends Projectile>> newProjectileType;
  private final Optional<PreparedNumberAction> velocityMod;
  private final Optional<String> kit;

  private final String metadataKey = "projectileKit";

  /**
   * @param holder of the facet
   * @param newProjectileType type to change projectile to
   * @param velocityMod velocity multiplier of new projectile
   * @param kit kit id to be applied when a player is hit
   */
  public ModifyProjectileFacet(
      FacetHolder holder,
      Optional<Class<? extends Projectile>> newProjectileType,
      Optional<PreparedNumberAction> velocityMod,
      Optional<String> kit) {
    this.holder = holder;
    this.newProjectileType = newProjectileType;
    this.velocityMod = velocityMod;
    this.kit = kit;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onProjectileLaunch(ProjectileLaunchEvent event) {
    if (!(event.getActor() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getActor();

    Projectile projectile = event.getEntity();

    if (newProjectileType.isPresent()
        && !projectile.getType().getEntityClass().equals(newProjectileType.get())) {
      event.setCancelled(true);
      // Technically this fires the event twice but the type check stops it from going any further
      // until the second time it fires with the correct type.
      projectile =
          player.launchProjectile(newProjectileType.get(), event.getEntity().getVelocity());
      return;
    }

    if (velocityMod.isPresent()) {
      Vector velocity = projectile.getVelocity();
      double oldMagnitude = velocity.length();
      double newMagnitude = velocityMod.get().perform(oldMagnitude);
      projectile.setVelocity(velocity.multiply(newMagnitude / oldMagnitude));
    }

    if (kit.isPresent()) {
      event.getEntity().setMetadata(metadataKey, new FixedMetadataValue(GamesPlugin.instance, kit));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player && event.getDamager().hasMetadata(metadataKey))) {
      return;
    }

    String kitId = event.getDamager().getMetadata(metadataKey, GamesPlugin.instance).asString();
    Optional<Kit> kit = holder.getRegistry().get(Kit.class, kitId, false);

    if (!kit.isPresent()) {
      GamesPlugin.instance
          .mapLogger()
          .log(Level.SEVERE, "Could not find kit id: " + kitId + " for modified projectile.");
    } else {
      kit.get().apply((Player) event.getEntity());
    }
  }
}
