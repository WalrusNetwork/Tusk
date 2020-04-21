package network.walrus.ubiquitous.bukkit.tracker.info;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.Cause;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.Fall.From;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Information describing the factors surrounding a {@link Fall}.
 *
 * @author Overcast Network
 */
public class GravityDamageInfo extends AbstractDamageInfo {

  private final @Nonnull Cause cause;
  private final @Nonnull From from;
  private final @Nullable Location fallLocation;

  /**
   * Constructor.
   *
   * @param resolvedDamager which caused the fall to begin
   * @param cause describing why the fall happened
   * @param from location describing where the fall began
   * @param fallLocation where the fall began
   */
  public GravityDamageInfo(
      @Nullable LivingEntity resolvedDamager,
      @Nonnull Cause cause,
      @Nonnull From from,
      @Nonnull Location fallLocation) {
    super(resolvedDamager);

    Preconditions.checkNotNull(resolvedDamager, "damager");
    Preconditions.checkNotNull(cause, "cause");
    Preconditions.checkNotNull(from, "from");

    this.cause = cause;
    this.from = from;
    this.fallLocation = fallLocation;
  }

  public @Nonnull Cause getCause() {
    return this.cause;
  }

  public @Nullable From getFrom() {
    return this.from;
  }

  public @Nonnull Location getFallLocation() {
    return this.fallLocation;
  }

  @Override
  public @Nonnull String toString() {
    return "GravityDamageInfo{damager="
        + this.resolvedDamager
        + ",cause="
        + this.cause
        + ",from="
        + this.from
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.FALL;
  }
}
