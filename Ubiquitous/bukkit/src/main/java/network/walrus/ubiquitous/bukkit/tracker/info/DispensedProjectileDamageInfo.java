package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Information about damage caused by a projectile that was dispensed from a dispenser.
 *
 * @author Overcast Network
 */
public class DispensedProjectileDamageInfo extends ProjectileDamageInfo {

  protected final @Nullable OfflinePlayer dispenserOwner;

  /**
   * Constructor.
   *
   * @param projectile which caused the damage
   * @param resolvedDamager who placed the dispenser
   * @param projectileDistance distance the projectile traveled before damage occurred
   * @param dispenserOwner player who owned the dispenser that dispensed the projectile
   */
  public DispensedProjectileDamageInfo(
      @Nonnull Projectile projectile,
      @Nullable LivingEntity resolvedDamager,
      @Nullable Double projectileDistance,
      @Nullable OfflinePlayer dispenserOwner) {
    super(projectile, resolvedDamager, projectileDistance);

    this.dispenserOwner = dispenserOwner;
  }

  public @Nullable OfflinePlayer getDispenserOwner() {
    return this.dispenserOwner;
  }

  @Override
  public @Nonnull String toString() {
    return "DispensedProjectileDamageInfo{shooter="
        + this.resolvedDamager
        + ",projectile="
        + this.projectile
        + ",distance="
        + this.projectileDistance
        + ",dispenserOwner="
        + this.dispenserOwner
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.PROJECTILE;
  }
}
