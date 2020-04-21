package network.walrus.games.uhc.facets.border;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Border;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Borders;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Facet which handles world borders.
 *
 * @author ShinyDialga
 */
public class BorderFacet extends Facet implements Listener {

  private List<WorldBorder> borders;
  private WorldBorder activeBorder;
  private WorldBorder cachedActiveBorder;
  private Optional<WorldBorder> cachedNextBorder;
  private UHCRound round;
  private GameTask task;

  /** @param holder which this facet is operating in */
  public BorderFacet(FacetHolder holder) {
    this.round = (UHCRound) holder;
  }

  private static boolean isOnBorder(Vector center, double radius, int x, int z) {
    double xMin = center.getX() - radius - 1;
    double xMax = center.getX() + radius;
    double zMin = center.getZ() - radius - 1;
    double zMax = center.getZ() + radius;

    boolean border = false;

    if (x == xMin || x == xMax) {
      border = true;
    }

    if (z == zMin || z == zMax) {
      border = true;
    }

    if (x < xMin || x > xMax) {
      border = false;
    }

    if (z < zMin || z > zMax) {
      border = false;
    }

    return border;
  }

  private static boolean isNearBorder(Vector center, double radius, int glassRadius, int x, int z) {
    double xMin = center.getX() - radius - 1 + glassRadius;
    double xMax = center.getX() + radius - glassRadius;
    double zMin = center.getZ() - radius - 1 + glassRadius;
    double zMax = center.getZ() + radius - glassRadius;

    return x <= xMin || x >= xMax || z <= zMin || z >= zMax;
  }

  @Override
  public void load() {
    borders = new ArrayList<>();
    int border = UHCManager.instance.getConfig().initialBorder.get();
    recalculateDefaultBorders(border);
  }

  @EventHandler
  public void onScatter(RoundStateChangeEvent event) {
    if (!event.getTo().isPresent() || !event.getTo().get().starting()) {
      return;
    }

    apply(borders.get(0));
  }

  @Override
  public void enable() {
    task =
        GameTask.of(
                "Border Applicator",
                () -> {
                  for (WorldBorder border : borders) {
                    if (border.applied) {
                      continue;
                    }
                    if (isBroadcastTime(border.duration.minus(this.round.getPlayingDuration()))) {
                      round
                          .getContainer()
                          .broadcast(
                              UHCMessages.BORDER_SHRINK.with(
                                  Borders.SHRINK_TEXT,
                                  new LocalizedNumber(border.radius, Borders.SHRINK_SIZE),
                                  new UnlocalizedText(
                                      StringUtils.secondsToClock(
                                          (int)
                                              (border.duration.minus(
                                                      this.round.getPlayingDuration()))
                                                  .getSeconds()),
                                      Borders.SHRINK_TIME)));
                      round.getContainer().broadcast(Border.SHRINK);
                    }
                    if (border.duration.compareTo(this.round.getPlayingDuration()) <= 0) {
                      apply(border);
                    }
                  }
                })
            .repeat(0, 20);
  }

  List<WorldBorder> getBorders() {
    return borders;
  }

  void recalculateDefaultBorders(int firstBorder) {
    borders.clear();
    int i = 0;
    int border = firstBorder;
    while (border > 50) {
      addBorder(new WorldBorder(border, Duration.ofSeconds((long) (i * 1080))));
      border = border - border / 2;
      i++;
    }
    if (borders.isEmpty()) {
      addBorder(new WorldBorder(firstBorder, Duration.ofSeconds(0)));
    }
  }

  void addBorder(WorldBorder border) {
    borders.add(border);
    borders.sort(Comparator.comparing(wb -> wb.duration));
    invalidateCaches();
    UbiquitousBukkitPlugin.getInstance().getDisplayManager().update("current-border");
  }

  void removeBorder(WorldBorder border) {
    borders.remove(border);
    borders.sort(Comparator.comparing(wb -> wb.duration));
    invalidateCaches();
    UbiquitousBukkitPlugin.getInstance().getDisplayManager().update("current-border");
  }

  private void apply(WorldBorder border) {
    WorldBorder oldBorder = activeBorder;
    activeBorder = border;
    activeBorder.applied = true;

    World world = round.getContainer().mainWorld();
    List<Vector> toSet = Lists.newArrayList();
    setSide(true, world, border, toSet);
    setSide(false, world, border, toSet);
    world.fastBlockChange(toSet, new MaterialData(Material.BEDROCK));

    if (oldBorder != null) {
      for (Player player : round.playingPlayers()) {
        if (player.isDead()) {
          continue;
        }
        Location location = player.getLocation();
        if (outOfBorder(location)) {
          teleportFarInside(player, border, oldBorder);
        }
      }
    }
    UbiquitousBukkitPlugin.getInstance().getDisplayManager().update("current-border");
  }

  private void setSide(boolean xPlane, World world, WorldBorder border, List<Vector> toSet) {
    int lower =
        (int) (xPlane ? border.center.getX() : border.center.getZ()) - (int) (border.radius) - 1;
    int upper =
        (int) (xPlane ? border.center.getX() : border.center.getZ()) + (int) (border.radius);

    for (int x = lower + 1; x < upper; x++) {
      double lowerHeight = 3;
      double upperHeight = 3;

      int lowerXCoord = xPlane ? x : lower;
      int lowerZCoord = xPlane ? lower : x;
      int upperXCoord = xPlane ? x : upper;
      int upperZCoord = xPlane ? upper : x;

      Location originalHighestLower =
          world.getHighestBlockAt(lowerXCoord, lowerZCoord).getLocation();
      Location originalHighestUpper =
          world.getHighestBlockAt(upperXCoord, upperZCoord).getLocation();
      Location actualHighestLower = highestBlock(originalHighestLower);
      Location actualHighestUpper = highestBlock(originalHighestUpper);
      lowerHeight = lowerHeight + originalHighestLower.getY() - actualHighestLower.getY();
      upperHeight = upperHeight + originalHighestUpper.getY() - actualHighestUpper.getY();
      toSet.add(actualHighestLower.toVector());
      toSet.add(actualHighestUpper.toVector());
      for (int lowerY = 0; lowerY <= lowerHeight; lowerY++) {
        toSet.add(actualHighestLower.clone().add(0, lowerY, 0).toVector());
      }
      for (int upperY = 0; upperY <= upperHeight; upperY++) {
        toSet.add(actualHighestUpper.clone().add(0, upperY, 0).toVector());
      }
    }
  }

  private void teleportFarInside(
      Player player, WorldBorder border, @Nullable WorldBorder oldBorder) {
    Location location = player.getLocation();
    location.setX(
        location.getX()
            * (border.radius / (oldBorder == null ? border.radius * 2 : oldBorder.radius))
            * 0.99);
    location.setZ(
        location.getZ()
            * (border.radius / (oldBorder == null ? border.radius * 2 : oldBorder.radius))
            * 0.99);
    Block highestBlock = round.getContainer().mainWorld().getHighestBlockAt(location);
    Location highestBlockLocation = highestBlock.getLocation();
    highestBlockLocation.subtract(0, 1, 0);

    if (round.playingPlayers().contains(player)) {
      highestBlockLocation.getBlock().setType(Material.SMOOTH_BRICK);
    }

    location.setY(highestBlockLocation.getY() + 2);

    player.teleport(location);
    Border.TELEPORT.play(player);
  }

  private void teleportRightInside(Player player, WorldBorder border) {
    double radius = border.radius;
    double xMin = activeBorder.center.getX() - radius + .5;
    double xMax = activeBorder.center.getX() + radius - .5;
    double zMin = activeBorder.center.getZ() - radius + .5;
    double zMax = activeBorder.center.getZ() + radius - .5;

    Location location = player.getLocation();
    double x = doubleRange(location.getX(), xMin, xMax);
    double z = doubleRange(location.getZ(), zMin, zMax);
    location.set(x, location.getY(), z);
    player.teleport(location);
  }

  private double doubleRange(double a, double min, double max) {
    return Double.min(Double.max(a, min), max);
  }

  private Location highestBlock(Location location) {
    Material material = location.getBlock().getType();
    if ((!material.isSolid() || material.isTransparent()) && location.getY() > 1) {
      return highestBlock(location.subtract(0, 1, 0));
    }
    switch (material) {
      case LOG:
      case LOG_2:
      case WOOD:
      case DARK_OAK_DOOR:
      case ACACIA_DOOR:
      case BIRCH_DOOR:
      case IRON_DOOR:
      case JUNGLE_DOOR:
      case SPRUCE_DOOR:
      case TRAP_DOOR:
      case WOOD_DOOR:
      case WOODEN_DOOR:
      case STAINED_GLASS_PANE:
        return highestBlock(location.subtract(0, 1, 0));
      default:
        break;
    }
    return location;
  }

  /** Keep players in the border */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerMove(PlayerCoarseMoveEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
      return;
    }

    if (activeBorder == null) {
      return;
    }

    Location location = event.getTo();
    if (!round.players().contains(event.getPlayer())) {
      return;
    }

    if (outOfBorder(location)) {
      event.setCancelled(true);
    }

    if (outOfBorder(event.getFrom()) && outOfBorder(location)) {
      teleportRightInside(event.getPlayer(), activeBorder);
      return;
    }

    int GLASS_RADIUS = 8;
    if (isNearBorder(
        activeBorder.center,
        activeBorder.radius,
        GLASS_RADIUS,
        location.getBlockX(),
        location.getBlockZ())) {
      for (int x = -GLASS_RADIUS; x <= GLASS_RADIUS; x++) {
        for (int z = -GLASS_RADIUS; z <= GLASS_RADIUS; z++) {
          int potentialX = location.getBlockX() + x;
          int potentialZ = location.getBlockZ() + z;
          if (isOnBorder(activeBorder.center, activeBorder.radius, potentialX, potentialZ)) {
            for (int y = -GLASS_RADIUS; y <= GLASS_RADIUS; y++) {
              int potentialY = location.getBlockY() + y;
              Location blockLocation =
                  new Location(
                      round.getContainer().mainWorld(), potentialX, potentialY, potentialZ);

              if (blockLocation.getBlock().getType().equals(Material.AIR)) {
                double distance = location.distance(blockLocation);

                Material material =
                    (distance > GLASS_RADIUS - 2) ? Material.AIR : Material.STAINED_GLASS;
                byte data = (distance > GLASS_RADIUS - 2) ? (byte) 0 : (byte) 14;
                if (!(material.equals(Material.STAINED_GLASS) && Math.abs(y) > 2)) {
                  event.getPlayer().sendBlockChange(blockLocation, material, data);
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onTeleport(PlayerTeleportEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
      return;
    }

    Location location = event.getTo();
    if (!round.players().contains(event.getPlayer())) {
      return;
    }
    if (outOfBorder(location)) {
      event.setCancelled(true);
    }
  }

  /** Don't allow blocks to be changed outside of the border */
  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockChange(BlockChangeByPlayerEvent event) {
    Location location = event.getBlock().getLocation();
    if (!round.players().contains(event.getPlayer())) {
      return;
    }
    if (outOfBorder(location)) {
      event.setCancelled(true);
    }
  }

  private boolean outOfBorder(Location location) {
    if (activeBorder == null) {
      return false;
    }
    double radius = activeBorder.radius;
    double xMin = activeBorder.center.getX() - radius;
    double xMax = activeBorder.center.getX() + radius;
    double zMin = activeBorder.center.getZ() - radius;
    double zMax = activeBorder.center.getZ() + radius;

    return location.getX() < xMin
        || location.getX() > xMax
        || location.getZ() < zMin
        || location.getZ() > zMax;
  }

  private boolean isBroadcastTime(Duration durationUntilShrink) {
    long minutes = durationUntilShrink.toMinutes();
    long seconds = durationUntilShrink.getSeconds() - (minutes * 60);

    return (minutes <= 5
        && (minutes == 5 && seconds == 0
            || minutes == 4 && seconds == 0
            || minutes == 3 && seconds == 0
            || minutes == 2 && seconds == 0
            || minutes == 1 && seconds == 0
            || minutes == 0 && seconds == 45
            || minutes == 0 && seconds == 30
            || minutes == 0 && seconds == 15
            || minutes == 0 && seconds == 10
            || minutes == 0 && seconds == 5
            || minutes == 0 && seconds == 4
            || minutes == 0 && seconds == 3
            || minutes == 0 && seconds == 2
            || minutes == 0 && seconds == 1));
  }

  private void invalidateCaches() {
    cachedActiveBorder = null;
  }

  public WorldBorder getActiveBorder() {
    if (activeBorder != null) {
      return activeBorder;
    } else {
      return getBorders().get(0);
    }
  }

  public Optional<WorldBorder> getNextBorder() {
    if (cachedActiveBorder == null
        || cachedActiveBorder != getActiveBorder()
        || cachedNextBorder == null) {
      cachedActiveBorder = getActiveBorder();

      for (int i = 0; i < borders.size(); i++) {
        if (borders.get(i) == cachedActiveBorder) {
          if (i + 1 >= borders.size()) {
            cachedNextBorder = Optional.empty();
          } else {
            cachedNextBorder = Optional.of(borders.get(i + 1));
          }

          return cachedNextBorder;
        }
      }

      throw new IllegalStateException("Active border is not in borders list");
    } else {
      return cachedNextBorder;
    }
  }
}
