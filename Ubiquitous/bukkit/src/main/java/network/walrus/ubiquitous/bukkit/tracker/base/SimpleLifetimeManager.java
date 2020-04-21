package network.walrus.ubiquitous.bukkit.tracker.base;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import org.bukkit.entity.LivingEntity;

/**
 * @author Overcast Network
 * @see LifetimeManager
 */
public class SimpleLifetimeManager implements LifetimeManager {

  private final Map<LivingEntity, Lifetime> lifetimes = new WeakHashMap<>();

  public @Nonnull Lifetime getLifetime(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    Lifetime lifetime = this.lifetimes.get(entity);
    if (lifetime == null) {
      lifetime = new SimpleLifetime();
      this.lifetimes.put(entity, lifetime);
    }

    return lifetime;
  }

  public @Nonnull Lifetime setLifetime(@Nonnull LivingEntity entity, @Nonnull Lifetime lifetime) {
    Preconditions.checkNotNull(entity, "entity");
    Preconditions.checkNotNull(lifetime, "lifetime");

    Lifetime old = this.lifetimes.put(entity, lifetime);
    if (old != null) {
      return old;
    } else {
      return new SimpleLifetime();
    }
  }

  public @Nonnull Lifetime newLifetime(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    Lifetime lifetime = new SimpleLifetime();
    this.lifetimes.put(entity, lifetime);

    return lifetime;
  }

  public @Nonnull Lifetime endLifetime(@Nonnull LivingEntity entity) {
    Preconditions.checkNotNull(entity, "entity");

    Lifetime old = this.lifetimes.remove(entity);
    if (old != null) {
      return old;
    } else {
      return new SimpleLifetime();
    }
  }

  @Override
  public void transferOwnership(LivingEntity original, LivingEntity newOwner) {
    Lifetime current = this.lifetimes.remove(original);
    if (current != null) {
      this.lifetimes.put(newOwner, current);
    }
  }
}
