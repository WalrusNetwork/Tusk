package network.walrus.ubiquitous.bukkit.tracker.trackers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

/**
 * Tracks all interactions with anvils.
 *
 * @author Overcast Network
 */
public interface AnvilTracker extends Tracker {

  /**
   * Determine if a {@link FallingBlock} has an owner.
   *
   * @param entity to check for ownership
   * @return if the entity has an owner
   */
  boolean hasOwner(@Nonnull FallingBlock entity);

  /**
   * Get the {@link OfflinePlayer} who currently owns an anvil. Will return {@code null} if {@link
   * #hasOwner(FallingBlock)} is {@code false}.
   *
   * @param anvil to get the owner for
   * @return owner of the anvil
   */
  @Nullable
  OfflinePlayer getOwner(@Nonnull FallingBlock anvil);

  /**
   * Associate an {@link OfflinePlayer} to a {@link FallingBlock}.
   *
   * @param anvil to set the player to own
   * @param offlinePlayer to set as owner
   * @return previous owner of the anvil
   */
  @Nullable
  OfflinePlayer setOwner(@Nonnull FallingBlock anvil, @Nullable OfflinePlayer offlinePlayer);

  /**
   * Determine if the person who placed the supplied block is known.
   *
   * @param block to get placer status from
   * @return if the block has a known placer
   */
  boolean hasPlacer(@Nonnull Block block);

  /**
   * Get the {@link OfflinePlayer} who placed an anvil. Will return {@code null} if {@link
   * #hasPlacer(Block)} is {@code false}.
   *
   * @param block to get the placer for
   * @return placer of the anvil
   */
  @Nullable
  OfflinePlayer getPlacer(@Nonnull Block block);

  /**
   * Associate an {@link OfflinePlayer} to the placement of a {@link Block}.
   *
   * @param block the player placed
   * @param offlinePlayer to set as placer
   * @return previous placer of the anvil
   */
  @Nullable
  OfflinePlayer setPlacer(@Nonnull Block block, @Nonnull OfflinePlayer offlinePlayer);

  /**
   * Unset the placer of a block.
   *
   * @param block to clear placer for
   * @return the player who was the placer
   */
  @Nonnull
  OfflinePlayer clearPlacer(@Nullable Block block);

  /**
   * Transfer ownership of all tracked objects from one player to another
   *
   * @param original owner of anvils to transfer from
   * @param newOwner to transfer the anvils to
   */
  void transferOwnership(OfflinePlayer original, OfflinePlayer newOwner);
}
