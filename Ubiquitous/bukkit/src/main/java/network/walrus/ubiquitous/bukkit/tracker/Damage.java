package network.walrus.ubiquitous.bukkit.tracker;

import java.time.Instant;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import org.bukkit.Location;

/**
 * Represents a single instance of damage to an entity.
 *
 * <p>Implementations should be immutable.
 *
 * @author Overcast Network
 */
public interface Damage {

  /**
   * Gets the amount of damage that was inflicted.
   *
   * <p>Note that this is the raw damage in half-hearts before dampeners (armor, potion effects) are
   * applied.
   *
   * <p>Contract specifies that the result is 0 or greater.
   *
   * @return Amount of damage inflicted
   */
  int getDamage();

  /**
   * Gets the location where the damage occurred.
   *
   * <p>Contract specifies that the result is never null.
   *
   * @return Location of damage
   */
  @Nonnull
  Location getLocation();

  /**
   * Gets the time that the damage occurred.
   *
   * <p>Contract specifies that the result is never null.
   *
   * @return Time of damage
   */
  @Nonnull
  Instant getTime();

  /**
   * Gets additional information regarding this damage.
   *
   * @return {@link DamageInfo} subclass describing the damage in more detail
   */
  @Nonnull
  DamageInfo getInfo();
}
