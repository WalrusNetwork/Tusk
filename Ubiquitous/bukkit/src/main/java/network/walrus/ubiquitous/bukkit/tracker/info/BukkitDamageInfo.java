package network.walrus.ubiquitous.bukkit.tracker.info;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolver;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Fallback object when no {@link DamageResolver}s could resolve the cause of a damage event.
 *
 * @author Overcast Network
 */
public class BukkitDamageInfo extends AbstractDamageInfo {

  private final @Nonnull DamageCause cause;

  /**
   * Constructor.
   *
   * @param cause of the damage
   */
  public BukkitDamageInfo(@Nonnull DamageCause cause) {
    super(null);

    Preconditions.checkNotNull(cause, "damage cause");

    this.cause = cause;
  }

  public @Nonnull DamageCause getCause() {
    return this.cause;
  }

  @Override
  public @Nonnull String toString() {
    return "BukkitDamageInfo{cause=" + this.cause + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return this.cause;
  }
}
