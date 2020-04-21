package network.walrus.ubiquitous.bukkit.tracker.trackers.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

/**
 * @author Overcast Network
 * @see OwnedMobTracker
 */
public class SimpleOwnedMobTracker extends AbstractTracker implements OwnedMobTracker {

  private final Map<LivingEntity, OfflinePlayer> ownedMobs = Maps.newHashMap();

  public boolean hasOwner(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    return this.ownedMobs.containsKey(entity);
  }

  public @Nullable OfflinePlayer getOwner(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    return this.ownedMobs.get(entity);
  }

  public @Nonnull OfflinePlayer setOwner(
      @Nonnull LivingEntity entity, @Nonnull OfflinePlayer player) {
    Preconditions.checkNotNull(entity, "entity");
    Preconditions.checkNotNull(player, "player");

    return this.ownedMobs.put(entity, player);
  }

  /**
   * Clear the owner information for a {@link LivingEntity}.
   *
   * @param entity to clear ownership from
   * @return player who owned the entity
   */
  public @Nonnull OfflinePlayer clearOwner(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    return this.ownedMobs.remove(entity);
  }

  public void clear(@Nonnull World world) {
    Preconditions.checkNotNull(world, "world");

    // clear information about owned mobs in that world
    for (Entry<LivingEntity, OfflinePlayer> livingEntityPlayerEntry : this.ownedMobs.entrySet()) {
      LivingEntity entity = livingEntityPlayerEntry.getKey();
      if (entity.getWorld().equals(world)) {
        entity.remove();
      }
    }
  }

  @Override
  public void transferOwnership(OfflinePlayer original, OfflinePlayer newOwner) {
    this.ownedMobs.replaceAll(
        (b, p) -> {
          if (p.getUniqueId().equals(original.getUniqueId())) {
            return newOwner;
          } else {
            return p;
          }
        });
  }
}
