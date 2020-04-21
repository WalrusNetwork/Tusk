package network.walrus.games.octc.hills;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.Hill.Capture;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.Hill.UnCapture;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

/**
 * Manages announcing who owns a certain hill, the user interface side of the hills
 *
 * @author Matthew Arnold
 */
public class HillAnnouncer {

  private final GameRound round;
  private final HillObjective hill;
  private GroupsManager groupsManager = null;

  /**
   * Creates a new hill announcer for a specific game round
   *
   * @param round the game round
   */
  public HillAnnouncer(HillObjective objective, GameRound round) {
    this.round = round;
    this.hill = objective;
  }

  public void announceHillCapture(Optional<Competitor> oldOwner) {
    if (groupsManager == null) {
      groupsManager = round.getFacetRequired(GroupsManager.class);
    }

    Localizable hillName =
        hill.getName()
            .toText(hill.owner().map(x -> x.getColor().getChatColor()).orElse(ChatColor.WHITE));

    if (hill.owner().isPresent()) {
      Localizable teamName = hill.owner().get().getColoredName();
      round.getContainer().broadcast(OCNMessages.CAPTURE_HILL.with(hillName, teamName));

      groupsManager.playScopedSound(
          hill.owner().get().getPlayers(),
          Capture.SELF,
          Capture.TEAM,
          Capture.ENEMY,
          Capture.SPECTATOR);
      spawnFirework(hill.owner().get().getColor().getFireworkColor());
    } else {
      round
          .getContainer()
          .broadcast(
              OCNMessages.UNCAPTURE_HILL.with(
                  oldOwner.get().getColoredName(),
                  hillName)); // safe get because if the new owner is absent, the old
      // one must exist

      Set<Player> source = Sets.newHashSet();
      if (hill.dominator().isPresent()) {
        source = hill.dominator().get().getPlayers();
      }
      groupsManager.playScopedSound(
          source, UnCapture.SELF, UnCapture.TEAM, UnCapture.ENEMY, UnCapture.SPECTATOR);
      spawnFirework(Color.WHITE);
    }
  }

  private void spawnFirework(Color color) {
    Vector vector = hill.options().progressRegion.getCenter();
    World world = round.getContainer().mainWorld();

    Location location = new Location(world, vector.getX(), vector.getY(), vector.getZ());

    Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(color);
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.setVelocity(firework.getVelocity().multiply(0.7));
    // PlayerUtils.broadcastSound(Sound.FIREWORK_BLAST2); //might be worth having this sound,
    // experiment and see if it's a good idea
  }
}
