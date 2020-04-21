package network.walrus.ubiquitous.bukkit.tracker.info;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Information about damage which was caused by a {@link Projectile}.
 *
 * @author Overcast Network
 */
public class ProjectileDamageInfo extends AbstractDamageInfo {

  protected final @Nonnull Projectile projectile;
  protected final @Nullable Double projectileDistance;

  /**
   * Constructor.
   *
   * @param projectile which caused the damage
   * @param resolvedDamager entity which caused the damage
   * @param projectileDistance distance the projectile traveled before doing damage
   */
  public ProjectileDamageInfo(
      @Nonnull Projectile projectile,
      @Nullable LivingEntity resolvedDamager,
      @Nullable Double projectileDistance) {
    super(resolvedDamager);

    Preconditions.checkNotNull(projectile, "projectile");

    this.projectile = projectile;
    this.projectileDistance = projectileDistance;
  }

  public @Nonnull Projectile getProjectile() {
    return this.projectile;
  }

  public @Nullable Double getDistance() {
    return this.projectileDistance;
  }

  @Override
  public @Nonnull String toString() {
    return "ProjectileDamageInfo{shooter="
        + this.resolvedDamager
        + ",projectile="
        + this.projectile
        + ",distance="
        + this.projectileDistance
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.PROJECTILE;
  }
}
