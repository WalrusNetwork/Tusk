package network.walrus.ubiquitous.bukkit.tracker.lifetime;

import java.time.Instant;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;

/**
 * Represents the lifetime of an entity.
 *
 * <p>Provides a convenient way to record damage objects pertaining to the victim.
 *
 * @author Overcast Network
 */
public interface Lifetime {

  /** @return when this life began */
  @Nullable
  Instant getStart();

  /**
   * Set the time when this life began.
   *
   * @param start when the life started
   */
  void setStart(@Nonnull Instant start);

  /** @return when this life is over */
  @Nullable
  Instant getEnd();

  /**
   * Set the time when this life ended.
   *
   * @param end when the life ended
   */
  void setEnd(@Nonnull Instant end);

  /** @return all of the damage that happened during this life, in order of dealt */
  @Nonnull
  List<Damage> getDamage();

  /** @return the first damage inflicted to this life in order of time */
  @Nonnull
  ListIterator<Damage> getDamageFirst();

  /** @return the most recent damage inflicted to this life in reverse order of time */
  @Nonnull
  ListIterator<Damage> getDamageLast();

  /** @return the first damage inflicted to this life */
  @Nullable
  Damage getFirstDamage();

  /** @return the most recent damage inflicted to this life */
  @Nullable
  Damage getLastDamage();

  /**
   * Gets the last damage instance where the info object is an instance of the specified class. Uses
   * {@link Class#isInstance(Object)} to check info objects.
   *
   * @param damageInfoClass DamageInfo class to check for
   * @return Last damage that matched or null if none matched
   * @throws NullPointerException if damageInfoClass is null
   */
  @Nullable
  Damage getLastDamage(@Nonnull Class<? extends DamageInfo> damageInfoClass);

  /**
   * Add daamage information to this life.
   *
   * @param damage to add
   */
  void addDamage(@Nonnull Damage damage);
}
