package network.walrus.games.octc.destroyables.objectives;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.StagnatedCompletionObjective;
import network.walrus.games.core.facets.objectives.touchable.TouchableObjective;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * An objective that's main way of being completed is by destroying a determined number of blocks.
 *
 * @author Austin Mayes
 */
public abstract class DestroyableObjective extends TouchableObjective
    implements StagnatedCompletionObjective {

  private final GameRound round;
  private final DestroyableProperties properties;
  private double completionDouble;
  private HashMap<Block, MaterialData> originals;
  private List<Block> remaining;
  private HashMap<Competitor, AtomicInteger> brokenBlocks = Maps.newHashMap();
  private HashMap<Competitor, AtomicDouble> completions = Maps.newHashMap();

  /**
   * @param round that the objective is in
   * @param properties describing the objective's attributes
   */
  public DestroyableObjective(GameRound round, DestroyableProperties properties) {
    super(round, properties.metrics);
    this.round = round;
    this.properties = properties;
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    if (this.properties.breakFilter.isPresent() && competitor.getGroup() instanceof Team) {
      Team team = (Team) competitor.getGroup();
      FilterContext context = new FilterContext();
      context.add(new GroupVariable(team));
      if (this.properties.breakFilter.get().test(context).fails()) {
        return false;
      }
    }
    return !this.properties.owner.isPresent() || !isOwner(competitor.getGroup());
  }

  /**
   * Determine if a player can even break (regardless of group) this objective.
   *
   * @param player who is attempting to break the objective
   * @param block the player is breaking
   * @return if the player can even attempt to break the block
   */
  public boolean canPlayerBreak(Player player, Block block) {
    if (!this.properties.materials.matches(block.getState())) {
      return false;
    }

    if (this.properties.breakFilter.isPresent()) {
      FilterContext context = new FilterContext();
      context.add(new PlayerVariable(player));
      context.add(new LocationVariable(block.getLocation()));
      return this.properties.breakFilter.get().test(context).passes();
    }
    return true;
  }

  /**
   * Record a block break for a player.
   *
   * @param player who broke the block
   */
  public void recordBreak(Player player) {
    Optional<Competitor> competitor =
        round.getFacetRequired(GroupsManager.class).getCompetitorOf(player);
    competitor.ifPresent(
        c -> {
          this.brokenBlocks.putIfAbsent(c, new AtomicInteger());
          this.brokenBlocks.get(c).addAndGet(1);
        });
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return this.completions.getOrDefault(competitor, new AtomicDouble()).get()
        >= this.properties.neededCompletion;
  }

  /**
   * Determine if a player can even repair (regardless of group) this objective.
   *
   * @param player who is attempting to repair the objective
   * @param block the player is place
   * @return if the player can even attempt to place the block
   */
  public boolean canPlayerRepair(Player player, Block block) {
    if (!this.properties.repairable) {
      return false;
    }

    if (this.properties.repairFilter.isPresent()) {
      FilterContext context = new FilterContext();
      context.add(new PlayerVariable(player));
      context.add(new LocationVariable(block.getLocation()));
      return this.properties.repairFilter.get().test(context).passes();
    }
    return true;
  }

  /**
   * Determine if the block is inside of this objective's region
   *
   * @param block to check
   * @return if the block is inside the region
   */
  public boolean isInside(Block block) {
    return this.originals.containsKey(block);
  }

  /**
   * Determine if the supplied block is still needing to be broken
   *
   * @param block to check
   * @return if the block needs to be broken
   */
  public boolean isRemaining(Block block) {
    return this.remaining.contains(block);
  }

  /**
   * Get the original state of a block as it was when this objective was initialized.
   *
   * @param block to get original state for
   * @return original state of the block
   */
  public MaterialData getOriginal(Block block) {
    return this.originals.get(block);
  }

  /**
   * Find and replace all materials in the objective using matchers.
   *
   * @param find material(s) to search for
   * @param replace materials to replace the found materials with
   */
  public void updateMaterial(MultiMaterialMatcher find, SingleMaterialMatcher replace) {
    this.properties.materials.replaceMaterial(find, replace);
    for (Map.Entry<Block, MaterialData> original : this.originals.entrySet()) {
      if (find.matches(original.getKey().getState())) {
        original.getKey().setType(replace.material());
        if (replace.isDataPresent()) {
          original.getValue().setData(replace.data().get());
        }
      }
    }
    for (Block remain : this.remaining) {
      if (find.matches(remain.getState())) {
        remain.setType(replace.material());
        if (replace.isDataPresent()) {
          remain.setData(replace.data().get());
        }
      }
    }
  }

  /**
   * Spawn fireworks for a specific competitor at the supplied block.
   *
   * @param block to spawn fireworks at
   * @param competitor to color the fireworks for
   */
  public void spawnFirework(Block block, Competitor competitor) {
    if (!this.properties.fireworks) {
      return;
    }

    Location location = block.getLocation().add(0.5, 0.5, 0.5);
    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(competitor.getColor().getFireworkColor());
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.playEffect(EntityEffect.FIREWORK_EXPLODE);
  }

  @Override
  public void initialize() {
    this.originals = new HashMap<>();
    this.remaining = new ArrayList<>();

    for (Vector point : this.properties.region) {
      Block block = point.toLocation(this.round.getContainer().mainWorld()).getBlock();

      if (this.properties.materials.matches(block.getState())) {
        this.originals.put(block, block.getState().getData());
        this.remaining.add(block);
      }
    }

    this.recalculateCompletion();
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    return Collections.singleton(this.properties.region.getCenter());
  }

  /**
   * Add a new block to the remaining blocks list.
   *
   * @param block to add to the remaining list
   */
  public void repair(Block block) {
    this.remaining.add(block);
  }

  /** Update completion statistics based on remaining blocks. */
  public void recalculateCompletion() {
    this.remaining.removeIf(block -> this.properties.completedState.matches(block.getState()));

    this.completionDouble = 1.0 - (double) this.remaining.size() / (double) this.originals.size();

    for (Entry<Competitor, AtomicInteger> entry : this.brokenBlocks.entrySet()) {
      Competitor c = entry.getKey();
      AtomicInteger b = entry.getValue();
      this.completions.putIfAbsent(c, new AtomicDouble());
      this.completions.get(c).set((double) b.get() / (double) this.originals.size());
    }

    if (!this.isIncremental()) {
      if (this.isCompleted()) {
        this.completionDouble = 1.0;
      } else if (this.isTouched()) {
        this.completionDouble = 0.5;
      } else {
        this.completionDouble = 0;
      }
    }
  }

  @Override
  public double getCompletion() {
    return this.getCompletionDouble();
  }

  /**
   * Get the {@link Competitor} with the most completion. Will return empty if no completion has
   * occurred or there is a tie for most.
   */
  @Override
  public Optional<Competitor> getHighestCompleter() {
    Optional<Competitor> most = Optional.empty();

    double highest = Double.MIN_VALUE;

    List<Competitor> ties = new ArrayList<>();

    for (Map.Entry<Competitor, AtomicDouble> entry : this.completions.entrySet()) {
      if (entry.getValue().get() == highest) {
        ties.add(entry.getKey());
      } else if (entry.getValue().get() > highest) {
        ties.clear();
        highest = entry.getValue().get();
        most = Optional.of(entry.getKey());
      }
    }

    if (ties.isEmpty()) {
      return most;
    } else {
      return Optional.empty();
    }
  }

  @Override
  public double getCompletion(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    return this.completions.getOrDefault(competitor, new AtomicDouble()).get();
  }

  /**
   * Method that is called by listeners when the objective is marked as completed.
   *
   * @param info about the event that caused to objective to become complete
   */
  public abstract void onComplete(DestroyableEventInfo info);

  /** @return if the objective's repair rules should be strictly enforced */
  public boolean shouldEnforceRepairRules() {
    return this.properties.repairable || this.properties.enforceAntiRepair;
  }

  public DestroyableProperties getProperties() {
    return properties;
  }

  private double getCompletionDouble() {
    return completionDouble;
  }

  protected HashMap<Block, MaterialData> getOriginals() {
    return originals;
  }

  public List<Block> getRemaining() {
    return remaining;
  }

  public HashMap<Competitor, AtomicInteger> getBrokenBlocks() {
    return brokenBlocks;
  }

  public HashMap<Competitor, AtomicDouble> getCompletions() {
    return completions;
  }

  public GameRound getRound() {
    return round;
  }

  /**
   * Determine if the supplied group owns this objective.
   *
   * @param group to check
   * @return if the group owns the objective
   */
  public boolean isOwner(Group group) {
    return this.properties.owner.isPresent() && this.properties.owner.get().equals(group);
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return properties.name;
  }
}
