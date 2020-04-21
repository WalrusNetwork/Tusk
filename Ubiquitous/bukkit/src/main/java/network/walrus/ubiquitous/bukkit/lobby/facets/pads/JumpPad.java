package network.walrus.ubiquitous.bukkit.lobby.facets.pads;

import com.google.common.base.Objects;
import java.util.Optional;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Lobby.JumpPads;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An area in the world which gives players a specified velocity when they enter it.
 *
 * @author Austin Mayes
 */
public class JumpPad {

  private final BoundedRegion where;
  private final Vector velocity;
  private final Optional<String> permission;
  private final Optional<Localizable> errorMessage;

  /**
   * @param where the velocity should be applied
   * @param velocity to apply to the player
   * @param permission needed to use the jump pad
   * @param errorMessage shown to players when they attempt to use the pad without permission
   */
  public JumpPad(
      BoundedRegion where,
      Vector velocity,
      Optional<String> permission,
      Optional<Localizable> errorMessage) {
    this.where = where;
    this.velocity = velocity;
    this.permission = permission;
    this.errorMessage = errorMessage;
  }

  public BoundedRegion getWhere() {
    return where;
  }

  /**
   * Attempt to use the pad for a specific player.
   *
   * @param player who is attempting to use the pad.
   */
  public void use(Player player) {
    if (this.permission.isPresent() && !player.hasPermission(this.permission.get())) {
      this.errorMessage.ifPresent(
          m -> {
            player.sendMessage(m);
            JumpPads.ERROR.play(player);
          });
      return;
    }

    player.setVelocity(this.velocity);
    JumpPads.USE.play(Bukkit.getOnlinePlayers(), player.getLocation());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JumpPad jumpPad = (JumpPad) o;
    return Objects.equal(where, jumpPad.where);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(where);
  }
}
