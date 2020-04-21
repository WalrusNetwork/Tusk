package network.walrus.ubiquitous.bukkit.tracker.lifetime;

import javax.annotation.Nonnull;
import org.bukkit.entity.LivingEntity;

/**
 * Main class responsible for managing the starting and ending of {@link Lifetime}s.
 *
 * @author Overcast Network
 */
public interface LifetimeManager {

  /**
   * Get the current {@link Lifetime} of an entity.
   *
   * @param entity to get the lifetime for
   * @return lifetime of the entity
   */
  @Nonnull
  Lifetime getLifetime(@Nonnull LivingEntity entity);

  /**
   * Set the current {@link Lifetime} of an entity.
   *
   * @param entity to set the lifetime for
   * @param lifetime to attach to the entity
   * @return lifetime of the entity
   */
  @Nonnull
  Lifetime setLifetime(@Nonnull LivingEntity entity, @Nonnull Lifetime lifetime);

  /**
   * Start a new lifetime and attach it to a {@link LivingEntity}.
   *
   * @param entity to start the lifetime for
   * @return a new lifetime attached to the entity
   */
  @Nonnull
  Lifetime newLifetime(@Nonnull LivingEntity entity);

  /**
   * End a lifetime.
   *
   * @param entity to end the lifetime for
   * @return the lifetime which was ended
   */
  @Nonnull
  Lifetime endLifetime(@Nonnull LivingEntity entity);

  /**
   * Transfer ownership of a lifetime from one player to another
   *
   * @param original owner to transfer from
   * @param newOwner to transfer to
   */
  void transferOwnership(LivingEntity original, LivingEntity newOwner);
}
