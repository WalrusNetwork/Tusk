package network.walrus.games.core.facets.kits.type;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.facets.kits.ReversibleKit;
import network.walrus.utils.core.math.PreparedNumberAction;
import org.bukkit.entity.Player;

/**
 * Kit used to modify a player's health attributes.
 *
 * @author Avicus Network
 */
public class HealthKit extends ReversibleKit {

  private final PreparedNumberAction health;
  private final PreparedNumberAction maxHealth;
  private final PreparedNumberAction healthScale;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param health used to modify the player's current health
   * @param maxHealth used to modify the player's max health
   * @param healthScale used to modify the player's health scale
   */
  public HealthKit(
      boolean force,
      @Nullable Kit parent,
      PreparedNumberAction health,
      PreparedNumberAction maxHealth,
      PreparedNumberAction healthScale) {
    super(force, parent);
    this.health = health;
    this.maxHealth = maxHealth;
    this.healthScale = healthScale;
  }

  @Override
  public void give(Player player, boolean force) {
    // Health
    if (this.maxHealth != null) {
      player.setMaxHealth(this.maxHealth.perform(player.getMaxHealth()));
    }
    if (this.health != null) {
      player.setHealth(Math.max(0.1, this.health.perform(player.getHealth())));
    }
    if (this.healthScale != null) {
      player.setHealthScale(this.healthScale.perform(player.getHealthScale()));
    }
  }

  @Override
  public void remove(Player player) {
    if (this.maxHealth != null) {
      player.setMaxHealth(20);
    }
    if (this.healthScale != null) {
      player.setHealthScaled(false);
    }
  }
}
