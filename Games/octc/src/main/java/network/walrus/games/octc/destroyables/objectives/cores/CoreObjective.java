package network.walrus.games.octc.destroyables.objectives.cores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.DestroyableProperties;
import network.walrus.games.octc.destroyables.objectives.cores.events.CoreLeakEvent;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.utils.bukkit.listener.EventUtil;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.modifiers.JoinRegion;
import network.walrus.utils.bukkit.region.shapes.BlockRegion;
import network.walrus.utils.bukkit.region.shapes.CuboidRegion;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTC.Leak;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.DTC;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * A container made up of {@link Material}s which must be broken in order for a {@link Material} to
 * be "leaked" into the specified {@link #leakArea} in order to be completed.
 *
 * @author Austin Mayes
 */
public class CoreObjective extends DestroyableObjective {

  private final BoundedRegion leakArea;
  private JoinRegion<BlockRegion> liquidRegion;
  private JoinRegion<CuboidRegion> liquidRegionPadded;
  private Material liquid;
  private List<Material> allowedLiquidTransformations = new ArrayList<>();
  private Optional<DestroyableEventInfo> lastBreak = Optional.empty();
  private boolean completed = false;

  /**
   * @param round that the objective is in
   * @param properties describing the objective's attributes
   * @param leakDistance distance needed for liquid to leak in order this objective to be completed
   */
  public CoreObjective(GameRound round, DestroyableProperties properties, int leakDistance) {
    super(round, properties);
    Vector leakMin =
        new Vector(properties.region.min().getX() - 4, 0, properties.region.min().getZ() - 4);
    Vector leakMax =
        new Vector(
            properties.region.max().getX() + 4,
            properties.region.min().getY() - leakDistance,
            properties.region.max().getZ() + 4);

    this.leakArea = new CuboidRegion(leakMin, leakMax);
  }

  @Override
  public void initialize() {
    super.initialize();

    List<BlockRegion> liquids = new ArrayList<>();

    this.getProperties()
        .region
        .iterator()
        .forEachRemaining(
            vector -> {
              Block block =
                  this.getRound()
                      .getContainer()
                      .mainWorld()
                      .getBlockAt(vector.toLocation(this.getRound().getContainer().mainWorld()));
              if (block.getType().equals(Material.LAVA)
                  || block.getType().equals(Material.STATIONARY_LAVA)) {
                liquids.add(new BlockRegion(vector));

                this.liquid = block.getType();
              }
            });

    if (liquids.isEmpty()) {
      GamesPlugin.instance
          .mapLogger()
          .severe("Core at " + getProperties().region.getCenter() + " contains no liquids!");
      liquids.add(new BlockRegion(new Vector(0, -12, 0)));
      this.liquid = Material.AIR;
    }
    this.liquidRegion = new JoinRegion<>(liquids);
    if (this.liquid.equals(Material.LAVA) || this.liquid.equals(Material.STATIONARY_LAVA)) {
      this.allowedLiquidTransformations.add(Material.LAVA);
      this.allowedLiquidTransformations.add(Material.STATIONARY_LAVA);
    } else {
      this.allowedLiquidTransformations.add(Material.WATER);
      this.allowedLiquidTransformations.add(Material.STATIONARY_WATER);
    }
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted()
        && lastBreak.isPresent()
        && competitor.hasPlayer(lastBreak.get().getActor());
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public LocalizedFormat getTouchMessage() {
    return OCNMessages.CORE_TOUCHED;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public void onComplete(DestroyableEventInfo info) {
    CoreLeakEvent event = new CoreLeakEvent(this, info);
    Localizable monumentName =
        getName()
            .toText(
                getProperties()
                    .owner
                    .map(t -> t.getColor().getChatColor())
                    .orElse(ChatColor.WHITE));
    this.getRound()
        .getContainer()
        .broadcast(
            OCNMessages.CORE_LEAKED.with(
                DTC.CORE_LEAKED, monumentName, new PersonalizedBukkitPlayer(info.getActor())));

    this.getRound()
        .getFacetRequired(GroupsManager.class)
        .playScopedSound(info.getActor(), Leak.SELF, Leak.TEAM, Leak.ENEMY, Leak.SPECTATOR);

    EventUtil.call(event);
  }

  public BoundedRegion getLeakArea() {
    return leakArea;
  }

  public JoinRegion<BlockRegion> getLiquidRegion() {
    return liquidRegion;
  }

  public JoinRegion<CuboidRegion> getLiquidRegionPadded() {
    return liquidRegionPadded;
  }

  public Material getLiquid() {
    return liquid;
  }

  public List<Material> getAllowedLiquidTransformations() {
    return allowedLiquidTransformations;
  }

  public Optional<DestroyableEventInfo> getLastBreak() {
    return lastBreak;
  }

  public void setLastBreak(Optional<DestroyableEventInfo> lastBreak) {
    this.lastBreak = lastBreak;
  }
}
