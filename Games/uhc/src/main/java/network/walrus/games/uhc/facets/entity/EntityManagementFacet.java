package network.walrus.games.uhc.facets.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCRound;
import network.walrus.ubiquitous.bukkit.tracker.tag.LoggerNPCManager;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Task that butchers mobs based on server TPS and modifies entity spawn rates as desired. Exposes
 * an API which allows developers to modify spawn rates as a percentage of a mob's normal spawn
 * rate. The modifier uses the mob specific modifier first, falling back to the friendly/hostile
 * base modifier (whose default is 1.0, standard spawn rates).
 *
 * @author Austin Mayes
 * @author Rafi Baum
 */
public class EntityManagementFacet extends Facet implements Listener {

  private static final Random random = new Random();
  private final UHCRound holder;
  private final GameTask task;
  private final Map<EntityType, Double> mobRateModifiers;
  private final int noHostileMobsSeconds;
  private boolean purge;
  private long ticksPerMonster;
  private long ticksPerAnimal;
  private long monsterLimit;
  private long animalLimit;
  private double friendlyMobModifier;
  private double hostileMobModifier;
  private boolean hostileMobsDisabled;

  /** @param holder which this object is managing entities for */
  public EntityManagementFacet(FacetHolder holder) {
    this.holder = (UHCRound) holder;
    this.task = GameTask.of("Entity management tick", this::tick);

    // Default base spawn rates
    friendlyMobModifier = 1.0;
    hostileMobModifier = .55;

    // Default specific spawn rates
    mobRateModifiers = Maps.newEnumMap(EntityType.class);
    mobRateModifiers.put(EntityType.SPIDER, .9);
    mobRateModifiers.put(EntityType.SKELETON, .65);
    mobRateModifiers.put(EntityType.WITCH, .6);

    mobRateModifiers.put(EntityType.COW, 1.4);
    mobRateModifiers.put(EntityType.CHICKEN, 1.5);
    mobRateModifiers.put(EntityType.SHEEP, .6);
    mobRateModifiers.put(EntityType.RABBIT, .5);

    hostileMobsDisabled = true;
    noHostileMobsSeconds = 60;
  }

  @Override
  public void load() {
    this.ticksPerAnimal = holder.getContainer().mainWorld().getTicksPerAnimalSpawns();
    this.ticksPerMonster = holder.getContainer().mainWorld().getTicksPerMonsterSpawns();
    this.monsterLimit = holder.getContainer().mainWorld().getMonsterSpawnLimit();
    this.animalLimit = holder.getContainer().mainWorld().getAnimalSpawnLimit();
    holder
        .getContainer()
        .actOnAllWorlds(
            w -> {
              w.setTicksPerAnimalSpawns(10000);
              w.setTicksPerMonsterSpawns(10000);
              w.setMonsterSpawnLimit(10000);
              w.setAnimalSpawnLimit(10000);
            });
    purge = true;
  }

  @Override
  public void enable() {
    purge = false;
    task.repeat(20 * 60, 3 * 20 * 60); // After 1 minute, every 3 minutes
    GameTask.of(
            "Entity management reset",
            () -> {
              holder.getContainer().mainWorld().setTicksPerAnimalSpawns((int) this.ticksPerAnimal);
              holder
                  .getContainer()
                  .mainWorld()
                  .setTicksPerMonsterSpawns((int) this.ticksPerMonster);
              holder.getContainer().mainWorld().setMonsterSpawnLimit((int) this.monsterLimit);
              holder.getContainer().mainWorld().setAnimalSpawnLimit((int) this.animalLimit);
            })
        .later(60 * 20);
    GameTask.of("Entity management enable-hostile", () -> hostileMobsDisabled = false)
        .later(20 * noHostileMobsSeconds);
  }

  @Override
  public void disable() {
    task.reset();
  }

  /** @return the default spawn modifier for friendly mobs */
  public double getFriendlyMobModifier() {
    return friendlyMobModifier;
  }

  /** @param modifier the default spawn modifier for friendly mobs */
  public void setFriendlyMobModifier(double modifier) {
    this.friendlyMobModifier = modifier;
  }

  /** @return the default spawn modifier for hostile mobs */
  public double getHostileMobModifier() {
    return hostileMobModifier;
  }

  /** @param hostileMobModifier the default spawn modifier for hostile mobs */
  public void setHostileMobModifier(double hostileMobModifier) {
    this.hostileMobModifier = hostileMobModifier;
  }

  /**
   * @param type mob type
   * @return the spawn rate modifier for that entity, defaulting to 1.0 if no modifier was specified
   */
  public double getEntityModifier(EntityType type) {
    return mobRateModifiers.getOrDefault(type, 1.0);
  }

  /**
   * @param type mob type
   * @param modifier the desired spawn rate modifier for that mob
   */
  public void setEntityModifier(EntityType type, double modifier) {
    mobRateModifiers.put(type, modifier);
  }

  private void tick() {
    double tps = Bukkit.getServer().spigot().getTPS()[0];
    if (tps < 12) {
      holder
          .getContainer()
          .actOnAllWorlds(
              world -> {
                if (world.getPlayers().isEmpty()) {
                  return;
                }
                for (Entity entity : world.getEntities()) {
                  if (entity instanceof Monster && !(entity instanceof Player)) {
                    if (LoggerNPCManager.isNPC(entity)) {
                      continue;
                    } else if (entity instanceof EnderDragon
                        || entity instanceof Pig
                        || entity instanceof Chicken
                        || entity instanceof Cow
                        || (entity instanceof Wolf && ((Wolf) entity).isTamed())
                        || (entity instanceof Horse)) {
                      continue;
                    }

                    entity.remove();
                  }
                }
                UHCManager.instance
                    .hostLogger()
                    .info("Butchered all monsters from " + world.getName());
              });
    } else if (tps < 17) {
      holder
          .getContainer()
          .actOnAllWorlds(
              world -> {
                if (world.getPlayers().isEmpty()) {
                  return;
                }
                for (Entity entity : world.getEntities()) {
                  if (entity instanceof Sheep || entity instanceof Squid || entity instanceof Bat) {
                    entity.remove();
                  }
                }
                UHCManager.instance
                    .hostLogger()
                    .info("Butchered ambient mobs from " + world.getName());
              });
    }
  }

  /** Purge entities on chunk load */
  @EventHandler
  public void onLoad(ChunkLoadEvent event) {
    if (purge)
      for (Entity entity : event.getChunk().getEntities()) {
        if (!(entity instanceof Player) && !LoggerNPCManager.isNPC(entity)) entity.remove();
      }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMobSpawn(CreatureSpawnEvent event) {
    if (!(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
        || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN)) {
      return;
    }

    if (hostileMobsDisabled && event.getEntity() instanceof Monster) {
      event.setCancelled(true);
      return;
    }

    int numToSpawn = numToSpawn(getModifier(event.getEntity()));

    if (numToSpawn == 0) {
      event.setCancelled(true);
    } else if (numToSpawn > 1) {
      for (int i = 1; i < numToSpawn; i++) {
        event.getWorld().spawnEntity(event.getLocation(), event.getEntityType());
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockPhysics(BlockPhysicsEvent event) {
    if (!holder.getState().playing()) {
      event.setCancelled(true);
    }
  }

  private double getModifier(LivingEntity entity) {
    if (entity instanceof Monster) {
      return mobRateModifiers.getOrDefault(entity.getType(), hostileMobModifier);
    } else {
      return mobRateModifiers.getOrDefault(entity.getType(), friendlyMobModifier);
    }
  }

  private int numToSpawn(double modifier) {
    if (Math.abs(modifier) < 0.001) {
      return 0;
    } else if (modifier < 1.0) {
      return random.nextDouble() < modifier ? 1 : 0;
    } else {
      return 1 + numToSpawn(modifier - 1.0);
    }
  }
}
