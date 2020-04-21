package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Information about damage caused by a mob which is owned by a player.
 *
 * @author Overcast Network
 */
public class OwnedMobDamageInfo extends AbstractDamageInfo {

  private final @Nullable LivingEntity mob;
  private final @Nullable OfflinePlayer owner;
  private final @Nullable Projectile projectile;

  /**
   * Constructor.
   *
   * @param owner player who owns the mob
   * @param mob mob who caused the damage
   * @param projectile projectile shot by the mob which caused the damage
   */
  public OwnedMobDamageInfo(
      @Nullable OfflinePlayer owner, @Nullable LivingEntity mob, @Nullable Projectile projectile) {
    super(chooseDamager(owner, mob));
    this.mob = mob;
    this.owner = owner;
    this.projectile = projectile;
  }

  // If player is online, they're responsible. Otherwise, mob is.
  private static LivingEntity chooseDamager(OfflinePlayer owner, LivingEntity mob) {
    if (owner != null && owner.isOnline()) {
      return owner.getPlayer();
    } else {
      return mob;
    }
  }

  /**
   * The resolved damager for an owned mob damage is the owner if they're still online, and the mob
   * otherwise.
   *
   * @return mob owner if they're still online, mob otherwise
   */
  @Nullable
  @Override
  public LivingEntity getResolvedDamager() {
    return chooseDamager(owner, mob);
  }

  /** @return the mob's owner */
  public @Nullable OfflinePlayer getMobOwner() {
    return owner;
  }

  /** @return the mob that caused the damage */
  public @Nullable LivingEntity getMob() {
    return mob;
  }

  /** @return projectile launched from the mob, or null if it doesn't exist */
  public @Nullable Projectile getProjectile() {
    return this.projectile;
  }

  @Override
  public @Nonnull String toString() {
    return "OwnedMobDamageInfo{damager="
        + this.resolvedDamager
        + ",owner="
        + this.owner
        + ",mob="
        + this.mob
        + ",projectile="
        + this.projectile
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    if (getProjectile() == null) {
      return DamageCause.ENTITY_ATTACK;
    } else {
      return DamageCause.PROJECTILE;
    }
  }
}
