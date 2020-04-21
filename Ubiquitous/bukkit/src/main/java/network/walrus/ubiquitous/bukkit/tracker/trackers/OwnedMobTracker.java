package network.walrus.ubiquitous.bukkit.tracker.trackers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Tracks all interactions with mobs.
 *
 * @author Overcast Network
 */
public interface OwnedMobTracker extends Tracker {

  /**
   * Determine if a {@link LivingEntity} has an owner.
   *
   * @param entity to check for ownership
   * @return if the entity has an owner
   */
  boolean hasOwner(@Nonnull LivingEntity entity);

  /**
   * Get the {@link Player} who currently owns an entity. Will return {@code null} if {@link
   * #hasOwner(LivingEntity)} is {@code false}.
   *
   * @param entity to get the owner for
   * @return owner of the tnt
   */
  @Nullable
  OfflinePlayer getOwner(@Nonnull LivingEntity entity);

  /**
   * Associate a {@link Player} to a {@link LivingEntity}.
   *
   * @param entity to set the player to own
   * @param player to set as owner
   * @return previous owner of the entity
   */
  @Nullable
  OfflinePlayer setOwner(@Nonnull LivingEntity entity, @Nullable OfflinePlayer player);

  /**
   * Transfer ownership of all tracked objects from one player to another
   *
   * @param original owner of mobs to transfer from
   * @param newOwner to transfer the mobs to
   */
  void transferOwnership(OfflinePlayer original, OfflinePlayer newOwner);
}
