package network.walrus.games.octc.ctf.flags;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.facets.kits.ReversibleKit;
import network.walrus.games.core.facets.objectives.GlobalObjective;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.facets.objectives.locatable.LocatableObjective;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.ctf.flags.events.FlagCaptureEvent;
import network.walrus.games.octc.ctf.flags.events.FlagDropEvent;
import network.walrus.games.octc.ctf.flags.events.FlagPickupEvent;
import network.walrus.games.octc.ctf.flags.events.FlagSpawnEvent;
import network.walrus.games.octc.ctf.flags.events.FlagStealEvent;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.color.ColorUtils;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTF.Capture;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTF.Drop;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTF.PickUp;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CTF;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Objectives;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

/**
 * An object which spawns at a {@link Post} which players need to pick up and carry to {@link Net}s.
 *
 * @author Austin Mayes
 */
public class FlagObjective extends LocatableObjective implements GlobalObjective, Objective {

  private static final BlockFace[] RADIAL = {
    BlockFace.SOUTH,
    BlockFace.SOUTH_WEST,
    BlockFace.WEST,
    BlockFace.NORTH_WEST,
    BlockFace.NORTH,
    BlockFace.NORTH_EAST,
    BlockFace.EAST,
    BlockFace.SOUTH_EAST
  };
  private static final int TICKS_PER_SAFE = 5;
  private static final int MAX_SAFE_DROPS = 20;

  private final Random RANDOM = new Random();
  private final FlagDistanceMetrics metrics;
  private final List<Post> posts;
  private final Optional<Team> owner;
  private final DyeColor color;
  private final ChatColor chatColor;
  private final LocalizedConfigurationProperty name;
  private final Duration recoverTime;
  private final Duration respawnTime;
  private final int carryingPoints;
  private final int pointsNeeded;
  private final List<Pattern> flagPatterns;
  private final boolean sequential;
  private final Deque<Location> safeDrops;
  private final Optional<Kit> carryingKit;
  private FlagsFacet facet;
  private SidebarFacet sidebar;
  private GroupsManager groupsManager;
  private int points;
  private Optional<Integer> currentPost = Optional.empty();
  private int curPost;
  private Optional<Player> carrier = Optional.empty();
  private ItemStack oldHelmet;
  private Optional<Location> dropLocation = Optional.empty();
  private Optional<FlagCountdown> flagCountdown = Optional.empty();
  private Optional<GameTask> rewardTask = Optional.empty();
  private int ticksToSave;

  /**
   * @param metrics being used to calculate distance for win calculation and UI
   * @param holder that the objective is inside of
   * @param posts that this flag can spawn at, with the first being the initial location at the
   *     start of the {@link network.walrus.games.core.round.GameRound}
   * @param owner of the flag
   * @param color to use for banner and chat messages
   * @param name to use for UI
   * @param carryingPoints points to reward every second to a player holding this flag
   * @param pointsNeeded points needed to complete the objective
   * @param recoverTime time until the flag respawns after being dropped
   * @param respawnTime time until the flag respawns after being captured
   * @param sequential whether flags should spawn sequentially
   * @param flagPatterns patterns the flag should have
   * @param carryingKit kit the flag carrier should receive
   */
  public FlagObjective(
      FlagDistanceMetrics metrics,
      FacetHolder holder,
      List<Post> posts,
      Optional<Team> owner,
      DyeColor color,
      LocalizedConfigurationProperty name,
      Duration recoverTime,
      Duration respawnTime,
      int carryingPoints,
      int pointsNeeded,
      boolean sequential,
      List<Pattern> flagPatterns,
      Optional<Kit> carryingKit) {
    super(metrics, holder);
    this.metrics = metrics;
    this.posts = posts;
    this.owner = owner;
    this.color = color;
    this.chatColor = ColorUtils.toChatColor(this.color);
    this.name = name;
    this.recoverTime = recoverTime;
    this.respawnTime = respawnTime;
    this.carryingPoints = carryingPoints;
    this.pointsNeeded = pointsNeeded;
    this.sequential = sequential;
    this.curPost = 0;
    this.flagPatterns = flagPatterns;
    this.safeDrops = new ArrayDeque<>(MAX_SAFE_DROPS);
    this.ticksToSave = 0;
    this.carryingKit = carryingKit;
  }

  @Override
  public void initialize() {
    super.initialize();
    this.facet = getHolder().getFacetRequired(FlagsFacet.class);
    this.sidebar = getHolder().getFacetRequired(SidebarFacet.class);
    this.groupsManager = getHolder().getFacetRequired(GroupsManager.class);
    place(this.posts.get(0));
  }

  /**
   * Pick the next {@link Post} that this flag should placed at when {@link #place()} is called.
   *
   * @param random if a random post should be chosen
   */
  private int pickNextPost(boolean random) {
    if (random) {
      return RANDOM.nextInt(this.posts.size());
    } else {
      if (curPost == this.posts.size() - 1) {
        return 0;
      } else {
        return curPost + 1;
      }
    }
  }

  @Override
  public boolean isCompleted() {
    return points >= pointsNeeded;
  }

  @Override
  public TextStyle distanceStyle(Competitor ref, Player viewer) {
    if (isCarrier(viewer)) {
      return Objectives.HOLDING;
    }
    if (ref instanceof Team) {
      return isCarrier((Team) ref) ? Objectives.TOUCHED : super.distanceStyle(ref, viewer);
    }
    return Objectives.UNTOUCHED;
  }

  @Override
  public double getCompletion() {
    return (double) points / pointsNeeded;
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    if (isCarried()) {
      return this.metrics.getCarryingMetric();
    } else {
      return this.metrics.getPreCompleteMetric();
    }
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return this.name;
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !this.owner.isPresent() || !this.owner.get().equals(competitor);
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return getCompletion();
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  // TODO: Locatable API needs to be improved
  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    return null;
  }

  @Override
  public String stringifyDistance(@Nullable Competitor ref, Player viewer, boolean sub) {
    if (ref == null && isCarried()) {
      return this.metrics.getCarryingMetric() == null
          ? ""
          : super.stringifyDistance(ref, viewer, sub);
    }

    if (!(ref instanceof Team)) {
      return super.stringifyDistance(ref, viewer, sub);
    }

    if (this.isCarrier((Team) ref)) {
      return this.metrics.getCarryingMetric() == null
          ? ""
          : super.stringifyDistance(ref, viewer, sub);
    } else {
      return super.stringifyDistance(ref, viewer, sub);
    }
  }

  /** @return the player currently holding this flag */
  public Optional<Player> getCarrierPlayer() {
    return this.carrier;
  }

  /**
   * @return the current location of this flag in the world. This is only empty when the flag has
   *     been captured and has not yet respawned.
   */
  public Optional<Location> getCurrentLocation() {
    if (this.carrier.isPresent()) {
      return getCarrierPlayer().map(Player::getLocation);
    }
    if (this.dropLocation.isPresent()) {
      return this.dropLocation;
    } else if (this.currentPost.isPresent()) {
      return Optional.of(
          this.posts
              .get(this.currentPost.get())
              .getRegion()
              .max()
              .toLocation(getHolder().getContainer().mainWorld()));
    }
    return Optional.empty();
  }

  /**
   * If someone could pick up the flag at the specified location.
   *
   * @param location to check
   * @return if someone can pickup the flag
   */
  public boolean canPickup(Location location) {
    if (this.carrier.isPresent()) {
      return false;
    } else if (this.dropLocation.isPresent()) {
      return dropLocation.get().distanceSquared(location) < 3.0;
    } else if (this.currentPost.isPresent()) {
      return this.posts.get(currentPost.get()).getRegion().contains(location);
    } else {
      return false;
    }
  }

  private ItemStack getBannerHeadItem() {
    ItemStack banner = new ItemStack(Material.BANNER);
    BannerMeta meta = (BannerMeta) banner.getItemMeta();
    meta.setBaseColor(getColor());
    meta.setPatterns(this.flagPatterns);
    banner.setItemMeta(meta);
    return banner;
  }

  /**
   * Notify the objective that a {@link Player} has picked it up.
   *
   * @param player who picked up the flag
   */
  public void pickup(Player player) {
    if (this.carrier.isPresent()) {
      throw new IllegalStateException(
          "Tried to pick flag up for "
              + player.getName()
              + " but flag is already being held by "
              + this.carrier.get().getName());
    }

    if (this.currentPost.isPresent()) {
      EventUtil.call(new FlagStealEvent(this, player));
    } else {
      EventUtil.call(new FlagPickupEvent(this, player));
    }

    cancelCountdown();

    clear();

    this.carrier = Optional.of(player);
    this.currentPost = Optional.empty();
    this.dropLocation = Optional.empty();
    facet.carry(player, this);

    this.safeDrops.clear();
    ticksToSave = 0;

    if (hasReward()) {
      this.rewardTask =
          Optional.of(GameTask.of("flag-carrier-points", this::reward).repeat(20, 20));
    }

    oldHelmet = player.getInventory().getHelmet();
    player.getInventory().setHelmet(getBannerHeadItem());

    getHolder()
        .getContainer()
        .broadcast(
            OCNMessages.FLAG_PICKUP.with(
                CTF.PICKUP,
                getName().toText(getChatColor()),
                new PersonalizedBukkitPlayer(player)));
    groupsManager.playScopedSound(player, PickUp.SELF, PickUp.TEAM, PickUp.ENEMY, PickUp.SPECTATOR);
    spawnFirework(player.getLocation());
    player.sendActionBar(OCNMessages.FLAG_CAPTURE_ACTIONBAR.with(CTF.CAPTURE).render(player));

    updateSidebar();
  }

  /**
   * Notify the objective that is has successfully been captured at a net.
   *
   * @param capturer player who captured the flag
   * @param net where flag was captured
   */
  public void capture(Player capturer, Net net) {
    if (!this.carrier.isPresent() || !this.carrier.get().equals(capturer)) {
      throw new IllegalStateException("Player must be carrying the flag to capture");
    }

    this.points += net.getReward();
    EventUtil.call(new FlagCaptureEvent(capturer, this));

    this.carrier.get().getInventory().setHelmet(oldHelmet);

    if (carryingKit.isPresent() && carryingKit.get() instanceof ReversibleKit) {
      ((ReversibleKit) carryingKit.get()).unapply(this.carrier.get());
    }

    this.carrier = Optional.empty();
    this.currentPost = Optional.empty();
    this.dropLocation = Optional.empty();
    facet.drop(capturer);

    if (this.rewardTask.isPresent()) {
      this.rewardTask.get().cancel();
      this.rewardTask = Optional.empty();
    }

    getHolder()
        .getContainer()
        .broadcast(
            OCNMessages.FLAG_CAPTURE.with(
                CTF.CAPTURE,
                getName().toText(getChatColor()),
                new PersonalizedBukkitPlayer(capturer)));
    groupsManager.playScopedSound(
        capturer, Capture.SELF, Capture.TEAM, Capture.ENEMY, Capture.SPECTATOR);
    spawnFirework(capturer.getLocation());

    if (this.respawnTime.getSeconds() > 0) {
      startCountdown(new FlagCountdown(this.respawnTime, this));
      updateSidebar();
    } else {
      place();
    }
  }

  /**
   * Place this flag at a certain location.
   *
   * @param vector to spawn the flag at
   * @param yaw to orient the flag to
   */
  public void spawn(Vector vector, float yaw) {
    Location location = vector.toLocation(getHolder().getContainer().mainWorld());
    location.setYaw(yaw);
    spawn(location);
  }

  /**
   * Place this flag at a certain location.*
   *
   * @param location to spawn the flag at
   */
  public void spawn(Location location) {
    Block bannerBlock = location.getBlock();
    bannerBlock.setType(Material.STANDING_BANNER);

    Banner data = (Banner) location.getBlock().getState();

    data.setBaseColor(getColor());
    data.setPatterns(this.flagPatterns);

    org.bukkit.material.Banner bannerMaterialData =
        (org.bukkit.material.Banner) data.getMaterialData();
    bannerMaterialData.setFacingDirection(RADIAL[Math.round(location.getYaw() / 45f) & 0x7]);

    data.update();

    Block baseBlock = location.clone().subtract(0, 1, 0).getBlock();
    if (baseBlock.getType() == Material.WATER || baseBlock.getType() == Material.STATIONARY_WATER) {
      baseBlock.setType(Material.ICE);
    }
  }

  /** Place the flag at the next post. */
  public void place() {
    int nextPost = pickNextPost(!sequential);
    place(posts.get(nextPost));
    curPost = nextPost;
  }

  private void place(Post post) {
    post.spawn(this);
    this.currentPost = Optional.of(posts.indexOf(post));
    EventUtil.call(new FlagSpawnEvent(this));
    updateSidebar();
  }

  /** Reward points to the current carrier of the flag. */
  public void reward() {
    this.points += carryingPoints;
  }

  /** Drop the flag at it's current location. */
  public void drop() {
    if (!isCarried()) {
      throw new RuntimeException("Can't drop an uncarried flag!");
    }

    Player dropper = this.carrier.get();
    dropper.getInventory().setHelmet(oldHelmet);

    Location candidateDropLocation = dropper.getEyeLocation();
    while (!canDropHere(candidateDropLocation)) {
      candidateDropLocation = safeDrops.removeFirst();
    }

    candidateDropLocation = candidateDropLocation.subtract(0, 1, 0).toBlockLocation();
    candidateDropLocation.setYaw(0);
    candidateDropLocation.setPitch(0);
    spawn(candidateDropLocation);

    if (carryingKit.isPresent() && carryingKit.get() instanceof ReversibleKit) {
      ((ReversibleKit) carryingKit.get()).unapply(this.carrier.get());
    }

    this.dropLocation = Optional.of(candidateDropLocation);
    this.currentPost = Optional.empty();
    this.carrier = Optional.empty();
    facet.drop(dropper);

    if (this.rewardTask.isPresent()) {
      this.rewardTask.get().cancel();
      this.rewardTask = Optional.empty();
    }

    if (this.recoverTime.getSeconds() > 0) {
      startCountdown(new FlagCountdown(this.recoverTime, this));
    }

    EventUtil.call(new FlagDropEvent(this, dropper));

    getHolder()
        .getContainer()
        .broadcast(OCNMessages.FLAG_DROP.with(CTF.DROP, getName().toText(getChatColor())));
    groupsManager.playScopedSound(dropper, Drop.SELF, Drop.TEAM, Drop.ENEMY, Drop.SPECTATOR);
    spawnFirework(candidateDropLocation);

    updateSidebar();
  }

  /** @return if the flag is currently being held by a player */
  public boolean isCarried() {
    return this.carrier.isPresent();
  }

  /**
   * Determine if a member of the given team is holding the flag.
   *
   * @param team to check
   * @return if someone on the given team is the {@link #carrier}
   */
  public boolean isCarrier(Team team) {
    return this.carrier.isPresent() && team.hasPlayer(this.carrier.get());
  }

  /**
   * Determine if the given player is currently holding this flag.
   *
   * @param player to check
   * @return if the player is holding the flag
   */
  public boolean isCarrier(Player player) {
    return this.carrier.isPresent() && this.carrier.get().equals(player);
  }

  /** @return if the flag is at a {@link Post} */
  public boolean isAtPost() {
    return this.currentPost.isPresent();
  }

  /**
   * @return if the flag is currently on the ground in the world, but not at a designated {@link
   *     Post}.
   */
  public boolean isDropped() {
    return this.dropLocation.isPresent();
  }

  void startCountdown(FlagCountdown countdown) {
    this.flagCountdown = Optional.of(countdown);
    UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(countdown);
  }

  private void cancelCountdown() {
    this.flagCountdown.ifPresent(
        countdown -> UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancel(countdown));
  }

  void clearCountdown() {
    this.flagCountdown = Optional.empty();
  }

  void updateSidebar() {
    for (Competitor competitor : groupsManager.getCompetitors()) {
      sidebar.update(FlagDisplay.flagSlug(this, Optional.of(competitor)));
    }

    sidebar.update(FlagDisplay.flagSlug(this, Optional.empty()));
  }

  private void spawnFirework(Location location) {
    Firework firework =
        (Firework)
            this.getHolder().getContainer().mainWorld().spawnEntity(location, EntityType.FIREWORK);
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

  private boolean canDropHere(Location location) {
    location = location.clone();
    Block topBlock = location.getBlock();
    if (!topBlock.isEmpty()) {
      return false;
    }

    Block bottomBlock = location.subtract(0, 1, 0).getBlock();
    if (!bottomBlock.isEmpty()) {
      return false;
    }

    Block baseBlock = location.subtract(0, 1, 0).getBlock();
    if (baseBlock.isEmpty()
        || baseBlock.getType() == Material.LAVA
        || baseBlock.getType() == Material.STATIONARY_LAVA) {
      return false;
    }

    return true;
  }

  void verifySafeDrop(Location location) {
    if (ticksToSave <= 0) {
      Block baseBlock = location.subtract(0, 1, 0).getBlock();
      while (baseBlock.getType() == Material.WATER
          || baseBlock.getType() == Material.STATIONARY_WATER) {
        baseBlock = location.add(0, 1, 0).getBlock();
      }
      location.add(0, 1, 0);

      if (canDropHere(location)) {
        if (safeDrops.size() == MAX_SAFE_DROPS) {
          safeDrops.removeLast();
        }

        safeDrops.addFirst(location);
        ticksToSave = TICKS_PER_SAFE;
      }
    } else {
      ticksToSave--;
    }
  }

  void clear() {
    Location flagBlock;

    if (this.currentPost.isPresent()) {
      flagBlock =
          posts
              .get(currentPost.get())
              .getVector()
              .toLocation(getHolder().getContainer().mainWorld());
      this.currentPost = Optional.empty();
    } else if (this.dropLocation.isPresent()) {
      flagBlock = this.dropLocation.get();
      this.dropLocation = Optional.empty();
    } else {
      return;
    }

    flagBlock.getBlock().setType(Material.AIR);
    Block baseBlock = flagBlock.subtract(0, 1, 0).getBlock();
    if (baseBlock.getType() == Material.ICE) {
      for (BlockFace face : BlockFace.values()) {
        // Ensure at least one surrounding block is water before melting
        Block faceBlock = baseBlock.getRelative(face);
        if (faceBlock.getType() == Material.WATER
            || faceBlock.getType() == Material.STATIONARY_WATER) {
          baseBlock.setType(Material.WATER);
          return;
        }
      }
    }
  }

  public ChatColor getChatColor() {
    return chatColor;
  }

  public Optional<Team> getOwner() {
    return owner;
  }

  public DyeColor getColor() {
    return color;
  }

  public int getPointsNeeded() {
    return pointsNeeded;
  }

  public int getPoints() {
    return points;
  }

  public Optional<Kit> getCarryingKit() {
    return carryingKit;
  }

  /** @return if this flag has a points reward for carrying players */
  public boolean hasReward() {
    return this.carryingPoints > 0;
  }

  public Optional<FlagCountdown> getCountdown() {
    return flagCountdown;
  }
}
