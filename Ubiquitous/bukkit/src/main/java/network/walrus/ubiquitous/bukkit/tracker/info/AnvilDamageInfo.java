package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Represents damage caused by an anvil falling on something.
 *
 * @author Overcast Network
 */
public class AnvilDamageInfo extends AbstractDamageInfo {

  private final @Nonnull FallingBlock anvil;
  private final @Nullable OfflinePlayer offlinePlayer;

  /**
   * Constructor.
   *
   * @param anvil which caused the damaged
   * @param resolvedDamager who caused the anvil to do the damage (if they are online)
   * @param offlinePlayer who caused the anvil to do the damage
   */
  public AnvilDamageInfo(
      @Nullable FallingBlock anvil,
      @Nullable LivingEntity resolvedDamager,
      @Nullable OfflinePlayer offlinePlayer) {
    super(resolvedDamager);

    this.anvil = anvil;
    this.offlinePlayer = offlinePlayer;
  }

  public @Nonnull FallingBlock getAnvil() {
    return this.anvil;
  }

  public @Nullable OfflinePlayer getOffinePlayer() {
    return this.offlinePlayer;
  }

  @Override
  public @Nonnull String toString() {
    return "AnvilDamageInfo{anvil="
        + this.anvil
        + ",damager="
        + this.resolvedDamager
        + ",offlinePlayer="
        + this.offlinePlayer
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.FALLING_BLOCK;
  }
}
