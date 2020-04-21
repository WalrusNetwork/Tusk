package network.walrus.games.octc.ctf.flags;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.util.GameTask;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Task which handles flag state variables which need to be updated per-tick.
 *
 * @author Austin Mayes
 */
public class FlagTask extends GameTask {

  private final List<FlagObjective> flags;

  /** @param flags */
  public FlagTask(List<FlagObjective> flags) {
    super("flag-task");
    this.flags = flags;
  }

  private static void playColoredParticle(Location location, DyeColor color) {
    PacketPlayOutWorldParticles packet =
        new PacketPlayOutWorldParticles(
            EnumParticle.ITEM_CRACK,
            true,
            (float) location.getX(),
            (float) location.getY() + 56,
            (float) location.getZ(),
            0.15f,
            24,
            0.15f,
            0f,
            40,
            Material.WOOL.getId(),
            color.getWoolData());

    for (Player player : location.getWorld().getPlayers()) {
      EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
      nmsPlayer.playerConnection.sendPacket(packet);
    }
  }

  /** Start the task */
  public void start() {
    repeat(0, 3);
  }

  @Override
  public void run() {
    for (FlagObjective flag : this.flags) {
      Optional<Location> optional = flag.getCurrentLocation();
      if (optional.isPresent()) {
        playColoredParticle(
            optional.get().clone().add(0, 2.0, 0).toCenterLocation(), flag.getColor());
      }

      Optional<Player> carrier = flag.getCarrierPlayer();
      Optional<Kit> carryingKit = flag.getCarryingKit();
      if (carrier.isPresent() && carryingKit.isPresent()) {
        carryingKit.get().apply(carrier.get());
      }
    }
  }
}
