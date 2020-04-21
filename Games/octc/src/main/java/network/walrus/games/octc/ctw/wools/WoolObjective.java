package network.walrus.games.octc.ctw.wools;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.GlobalObjective;
import network.walrus.games.core.facets.objectives.touchable.TouchableDistanceMetrics;
import network.walrus.games.core.facets.objectives.touchable.TouchableObjective;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.color.ColorUtils;
import network.walrus.utils.bukkit.inventory.MaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

/**
 * A {@link Material} which is located an optional source which must be picked up and carried by a
 * {@link Player} tp a specific {@link BoundedRegion} where is must be placed in order to be marked
 * as {@link #isCompleted()}.
 *
 * @author Austin Mayes
 */
public class WoolObjective extends TouchableObjective implements GlobalObjective {

  private final GameRound round;
  private final LocalizedConfigurationProperty name;
  private final Optional<Team> team;
  private final DyeColor color;
  private final Optional<BoundedRegion> source;
  private final BoundedRegion destination;
  private final boolean refill;
  private final int maxRefill;
  private final Optional<Duration> refillDelay;
  private final boolean craftable;
  private final boolean fireworks;
  private final MaterialMatcher matcher;
  private final Map<Block, Map<Integer, ItemStack>> refills;
  private final Map<UUID, Inventory> enderWoolInventories;
  private final int startWools;
  private final int woolsPerKill;
  private KillTracker killTracker;
  private boolean placed;
  private WoolsFacet facet;

  /**
   * @param round which this objective exists in
   * @param metrics used to track proximity for this objective
   * @param team which needs to complete this objective
   * @param color of the wool
   * @param source used to refill chests
   * @param destination where the wool needs to be placed
   * @param refill if the chests in the source region should automatically refill with wool
   * @param maxRefill maximum amount of wool that should be refilled
   * @param refillDelay amount of time before wools are refilled
   * @param craftable if the wool can be crafted in a work bench
   * @param fireworks if fireworks should be spawned at the destination when the wool is placed
   * @param startWools wools each player should start with
   * @param woolsPerKill number of wools each player should get per kill
   */
  public WoolObjective(
      GameRound round,
      TouchableDistanceMetrics metrics,
      Optional<Team> team,
      DyeColor color,
      Optional<BoundedRegion> source,
      BoundedRegion destination,
      boolean refill,
      int maxRefill,
      Optional<Duration> refillDelay,
      boolean craftable,
      boolean fireworks,
      int startWools,
      int woolsPerKill) {
    super(round, metrics);
    this.round = round;
    this.name = new LocalizedConfigurationProperty(OCNMessages.forWoolColor(color));
    this.team = team;
    this.color = color;
    this.source = source;
    this.destination = destination;
    this.refill = refill;
    this.maxRefill = maxRefill;
    this.refillDelay = refillDelay;
    this.craftable = craftable;
    this.fireworks = fireworks;
    this.startWools = startWools;
    this.woolsPerKill = woolsPerKill;

    this.matcher = new SingleMaterialMatcher(Material.WOOL, this.color.getWoolData());
    this.refills = Maps.newHashMap();

    this.enderWoolInventories = Maps.newHashMap();
  }

  public ChatColor getChatColor() {
    return ColorUtils.toChatColor(this.color);
  }

  /**
   * Called after all necessary checks have passed when a player successfully places this wool.
   *
   * @param who is placing the wool
   */
  public void place(Player who) {
    Preconditions.checkArgument(!this.placed, "wool already placed");
    this.placed = true;

    if (this.fireworks) {
      spawnFirework();
    }

    Group group = this.round.getFacetRequired(GroupsManager.class).getGroup(who);

    Localizable woolName = this.name.toText(getChatColor());
    UnlocalizedText teamName = new UnlocalizedText(who.getName(), group.getColor().style());

    LocalizedText broadcast = OCNMessages.WOOL_PLACED.with(woolName, teamName);
    this.round.getContainer().broadcast(broadcast);
  }

  private void spawnFirework() {
    Location location =
        this.destination.getCenter().toLocation(this.round.getContainer().mainWorld());
    Firework firework =
        (Firework) this.round.getContainer().mainWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(this.color.getFireworkColor());
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.setVelocity(firework.getVelocity().multiply(0.7));
    PlayerUtils.broadcastSound(Sound.FIREWORK_BLAST2);
  }

  /**
   * Determine if a block should be re-filled with wools.
   *
   * @param block to check
   * @return if the block should be re-filled
   */
  public boolean isRefillable(Block block) {
    return this.refills.containsKey(block);
  }

  /**
   * Get the state of a certain {@link Block} that was captured when this object was initialized
   * which should be used as a reference to refill items. The map uses the slot as the key and item
   * that should be in the slot as the value.
   *
   * @param block to get refill state for
   * @return refill state of a block, if a refill block exists
   */
  public Optional<Map<Integer, ItemStack>> getRefill(Block block) {
    return Optional.ofNullable(this.refills.get(block));
  }

  @Override
  public void initialize() {
    super.initialize();
    if (!this.source.isPresent() || !this.refill) {
      return;
    }
    for (Vector vector : this.source.get()) {
      Block block = vector.toLocation(this.round.getContainer().mainWorld()).getBlock();
      if (!(block.getState() instanceof InventoryHolder)) {
        continue;
      }

      InventoryHolder chest = (InventoryHolder) block.getState();

      Map<Integer, ItemStack> items = new HashMap<>();
      this.refills.put(block, items);
      for (int i = 0; i < chest.getInventory().getSize(); i++) {
        ItemStack item = chest.getInventory().getItem(i);
        if (item != null && this.matcher.matches(item.getData())) {
          items.put(i, chest.getInventory().getItem(i).clone());
        }
      }
    }
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !this.team.isPresent() || this.team.get().equals(competitor.getGroup());
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    if (hasTouchedRecently(base)) {
      return Collections.singleton(this.destination.getCenter());
    } else if (this.source.isPresent()) {
      return Collections.singleton(this.source.get().getCenter());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return isCompleted(competitor) ? 1.0 : 0;
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public boolean isCompleted() {
    return isPlaced();
  }

  @Override
  public double getCompletion() {
    if (isCompleted()) {
      return 1.0;
    } else if (isTouched()) {
      return 0.5;
    } else {
      return 0;
    }
  }

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if (!this.source.isPresent() && !isTouched()) {
      return false;
    } else {
      return super.shouldShowDistance(ref, viewer);
    }
  }

  @Override
  public LocalizableFormat getTouchMessage() {
    return OCNMessages.WOOL_PICKUP;
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return name;
  }

  public DyeColor getColor() {
    return color;
  }

  public Optional<BoundedRegion> getSource() {
    return source;
  }

  public BoundedRegion getDestination() {
    return destination;
  }

  public boolean isRefill() {
    return refill;
  }

  public int getMaxRefill() {
    return maxRefill;
  }

  public Optional<Duration> getRefillDelay() {
    return refillDelay;
  }

  public boolean isPlaced() {
    return placed;
  }

  public MaterialMatcher getMatcher() {
    return matcher;
  }

  public boolean isCraftable() {
    return craftable;
  }

  public Optional<Team> getTeam() {
    return team;
  }

  /**
   * Fetches or generates the wool chest for a specified player.
   *
   * @param player
   * @return the player-specific wool chest inventory
   */
  public Inventory getWoolInventory(Player player) {
    return enderWoolInventories.computeIfAbsent(
        player.getUniqueId(),
        (uuid) -> {
          // Cache kill tracker reference
          if (killTracker == null) {
            killTracker =
                this.round.getFacetRequired(StatsFacet.class).getTracker(KillTracker.class).get();
          }

          // Fill wool chest
          int wools = startWools + killTracker.getKills(uuid) * woolsPerKill;
          Inventory enderChest = Bukkit.createInventory(null, InventoryType.ENDER_CHEST);
          fillWoolInventory(enderChest, wools);

          // Cache facet reference
          if (facet == null) {
            facet = round.getFacetRequired(WoolsFacet.class);
          }

          // Associate inventory with objective
          facet.addWoolInventory(enderChest, this);

          return enderChest;
        });
  }

  void increaseWoolAllowance(Player player) {
    Inventory woolStorage = enderWoolInventories.get(player.getUniqueId());
    if (woolStorage == null) {
      return;
    }

    fillWoolInventory(woolStorage, woolsPerKill);
  }

  int fillWoolInventory(Inventory inventory, int wools) {
    ItemStack wool = new ItemStack(Material.WOOL, 1, getColor().getData());
    int slot = 0;

    while (wools > 0 && slot < inventory.getSize()) {
      ItemStack item = inventory.getItem(slot);
      if (item == null || item.getType() == Material.AIR) {
        inventory.setItem(slot, wool);
        wools--;
      }
      slot++;
    }

    return wools;
  }
}
