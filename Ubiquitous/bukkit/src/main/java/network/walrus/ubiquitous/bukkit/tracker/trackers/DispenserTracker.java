package network.walrus.ubiquitous.bukkit.tracker.trackers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * Tracks all interactions with dispensers.
 *
 * @author Overcast Network
 */
public interface DispenserTracker extends Tracker {

  /**
   * Determine if a {@link Entity} has an owner.
   *
   * @param entity to check for ownership
   * @return if the entity has an owner
   */
  boolean hasOwner(@Nonnull Entity entity);

  /**
   * Get the {@link OfflinePlayer} who currently owns an entity. Will return {@code null} if {@link
   * #hasOwner(Entity)} is {@code false}.
   *
   * @param entity to get the owner for
   * @return owner of the entity
   */
  @Nullable
  OfflinePlayer getOwner(@Nonnull Entity entity);

  /**
   * Associate an {@link OfflinePlayer} to an {@link Entity}.
   *
   * @param entity to set the player to own
   * @param player to set as owner
   * @return previous owner of the entity
   */
  @Nullable
  OfflinePlayer setOwner(@Nonnull Entity entity, @Nullable OfflinePlayer player);

  /**
   * Determine if the person who created the supplied entity is known.
   *
   * @param block to get placer status from
   * @return if the block has a known placer
   */
  boolean hasPlacer(@Nonnull Block block);

  /**
   * Get the {@link OfflinePlayer} who placed a dispenser. Will return {@code null} if {@link
   * #hasPlacer(Block)} is {@code false}.
   *
   * @param block to get the placer for
   * @return placer of the block
   */
  @Nullable
  OfflinePlayer getPlacer(@Nonnull Block block);

  /**
   * Associate an {@link OfflinePlayer} to the placement of a {@link Block}.
   *
   * @param block the player placed
   * @param player to set as placer
   * @return previous placer of the block
   */
  @Nullable
  OfflinePlayer setPlacer(@Nonnull Block block, @Nullable OfflinePlayer player);

  /**
   * Unset the placer of a block.
   *
   * @param block to clear placer for
   * @return the player who was the placer
   */
  @Nonnull
  OfflinePlayer clearPlacer(@Nonnull Block block);

  /**
   * Transfer ownership of all tracked objects from one player to another
   *
   * @param original owner of dispensers to transfer from
   * @param newOwner to transfer the dispensers to
   */
  void transferOwnership(OfflinePlayer original, OfflinePlayer newOwner);
}
