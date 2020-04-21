package network.walrus.ubiquitous.bukkit.tracker.trackers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;

/**
 * Tracks all interactions with explosives.
 *
 * @author Overcast Network
 */
public interface ExplosiveTracker extends Tracker {

  /**
   * Determine if a {@link TNTPrimed} has an owner.
   *
   * @param entity to check for ownership
   * @return if the tnt has an owner
   */
  boolean hasOwner(@Nonnull TNTPrimed entity);

  /**
   * Get the {@link OfflinePlayer} who currently owns a primed tnt. Will return {@code null} if
   * {@link #hasOwner(TNTPrimed)} is {@code false}.
   *
   * @param entity to get the owner for
   * @return owner of the tnt
   */
  @Nullable
  OfflinePlayer getOwner(@Nonnull TNTPrimed entity);

  /**
   * Associate an {@link OfflinePlayer} to an {@link TNTPrimed}.
   *
   * @param entity to set the player to own
   * @param player to set as owner
   * @return previous owner of the tnt
   */
  @Nullable
  OfflinePlayer setOwner(@Nonnull TNTPrimed entity, @Nullable OfflinePlayer player);

  /**
   * Determine if the person who placed the block is known.
   *
   * @param block to get placer status from
   * @return if the block has a known placer
   */
  boolean hasPlacer(@Nonnull Block block);

  /**
   * Get the {@link OfflinePlayer} who placed an explosive. Will return {@code null} if {@link
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
   * Transfer ownership of all tracked objects from one player to another
   *
   * @param original owner of explosives to transfer from
   * @param newOwner to transfer the explosives to
   */
  void transferOwnership(OfflinePlayer original, OfflinePlayer newOwner);
}
