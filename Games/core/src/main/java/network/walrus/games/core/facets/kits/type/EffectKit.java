package network.walrus.games.core.facets.kits.type;

import java.util.List;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.facets.kits.ReversibleKit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Kit used to give potion effects to players.
 *
 * @author Avicus Network
 */
public class EffectKit extends ReversibleKit {

  private final List<PotionEffect> effects;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param effects to apply to the player
   */
  public EffectKit(boolean force, @Nullable Kit parent, List<PotionEffect> effects) {
    super(force, parent);
    this.effects = effects;
  }

  @Override
  public void give(Player player, boolean force) {
    // Effects
    for (PotionEffect effect : this.effects) {
      player.addPotionEffect(effect, this.isForce() || force);
    }
  }

  @Override
  public void remove(Player player) {
    for (PotionEffect effect : this.effects) {
      player.removePotionEffect(effect.getType());
    }
  }
}
