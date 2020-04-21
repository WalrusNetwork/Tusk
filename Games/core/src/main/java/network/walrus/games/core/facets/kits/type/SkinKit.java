package network.walrus.games.core.facets.kits.type;

import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

/**
 * Kit used to modify a player's skin.
 *
 * @author Austin Mayes
 */
public class SkinKit extends Kit {

  private final Optional<Skin> skin;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param skin that the player should be given
   */
  public SkinKit(boolean force, @Nullable Kit parent, Optional<Skin> skin) {
    super(force, parent);
    this.skin = skin;
  }

  @Override
  public void give(Player player, boolean force) {
    if (this.skin.isPresent()) player.setSkin(this.skin.get());
    else player.setSkin(player.getRealSkin());
  }
}
