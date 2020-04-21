package network.walrus.utils.bukkit;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Utilities for going to the danger zone of Minecraft server development.
 *
 * @author Avicus Network
 */
public class NMSUtils {

  // These entity type IDs are hard-coded in a huge conditional statement in EntityTrackerEntry.
  // There is no nice way to get at them.
  private static final Map<Class<? extends org.bukkit.entity.Entity>, Integer> ENTITY_TYPE_IDS =
      ImmutableMap.of(
          org.bukkit.entity.Item.class, 2,
          ArmorStand.class, 78);
  private static Random random = new Random();

  /**
   * Send a raw packet to a player asynchronously using their raw connection instance.
   *
   * @param bukkitPlayer to send the packet to
   * @param packet to send
   */
  public static void sendPacket(Player bukkitPlayer, Object packet) {
    if (bukkitPlayer.isOnline()) {
      EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
      Bukkit.getScheduler()
          .runTaskAsynchronously(
              WalrusBukkitPlugin.getWalrusPlugin(),
              () -> nmsPlayer.playerConnection.sendPacket((Packet) packet));
    }
  }

  private static EntityTrackerEntry getTrackerEntry(Entity nms) {
    return ((WorldServer) nms.getWorld()).getTracker().trackedEntities.get(nms.getId());
  }

  private static EntityTrackerEntry getTrackerEntry(org.bukkit.entity.Entity entity) {
    return getTrackerEntry(((CraftEntity) entity).getHandle());
  }

  private static void sendPacketToViewers(Entity entity, Object packet) {
    EntityTrackerEntry entry = getTrackerEntry(entity);
    Set<EntityPlayer> viewers = Sets.newHashSet(entry.trackedPlayers);
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            WalrusBukkitPlugin.getWalrusPlugin(),
            () -> {
              for (EntityPlayer viewer : viewers) {
                viewer.playerConnection.sendPacket((Packet) packet);
              }
            });
  }

  private static double randomEntityVelocity() {
    return random.nextDouble() - 0.5d;
  }

  private static byte encodeAngle(float angle) {
    return (byte) (angle * 256f / 360f);
  }

  private static int encodeVelocity(double v) {
    return (int) (v * 8000D);
  }

  private static byte encodePosition(double d) {
    return (byte) (d * 4096D);
  }

  private static net.minecraft.server.v1_8_R3.Packet spawnEntityPacket(
      Class<? extends org.bukkit.entity.Entity> type,
      int data,
      int entityId,
      UUID uuid,
      Location location,
      Vector velocity) {
    checkArgument(ENTITY_TYPE_IDS.containsKey(type));
    return new net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity(
        entityId,
        location.getX(),
        location.getY(),
        location.getZ(),
        encodeVelocity(velocity.getX()),
        encodeVelocity(velocity.getY()),
        encodeVelocity(velocity.getZ()),
        encodeAngle(location.getPitch()),
        encodeAngle(location.getYaw()),
        ENTITY_TYPE_IDS.get(type),
        data);
  }

  private static net.minecraft.server.v1_8_R3.Packet setPassengerPacket(
      Entity vehicle, Entity rider) {
    return new PacketPlayOutAttachEntity(0, vehicle, rider);
  }

  private static net.minecraft.server.v1_8_R3.Packet moveEntityRelativePacket(
      int entityId, Vector delta, boolean onGround) {
    return new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
        entityId,
        encodePosition(delta.getX()),
        encodePosition(delta.getY()),
        encodePosition(delta.getZ()),
        onGround);
  }

  private static net.minecraft.server.v1_8_R3.Packet teleportEntityPacket(
      int entityId, Location location) {
    return new PacketPlayOutEntityTeleport(
        entityId,
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ(),
        encodeAngle(location.getYaw()),
        encodeAngle(location.getPitch()),
        true);
  }

  private static net.minecraft.server.v1_8_R3.Packet entityMetadataPacket(
      int entityId, net.minecraft.server.v1_8_R3.Entity nmsEntity, boolean complete) {
    return new net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata(
        entityId, nmsEntity.getDataWatcher(), complete);
  }

  private static net.minecraft.server.v1_8_R3.Packet entityMetadataPacket(
      net.minecraft.server.v1_8_R3.Entity nmsEntity, boolean complete) {
    return entityMetadataPacket(nmsEntity.getId(), nmsEntity, complete);
  }

  /**
   * Send out a collection of fake item entices to a player with a random velocity and a defined
   * lifetime before they are de-spawned. These items cannot be picked up but will be moved by world
   * events. The items will not be combined into one stack while on the ground.
   *
   * @param plugin which will be used to schedule item removal with bukkit
   * @param viewer to send the items to
   * @param location to use as a base to spawn items at
   * @param item type to spawn
   * @param count of fake items to spawn
   * @param duration until the items are removed from the world
   */
  public static void showFakeItems(
      Plugin plugin,
      Player viewer,
      Location location,
      org.bukkit.inventory.ItemStack item,
      int count,
      Duration duration) {
    if (count <= 0) {
      return;
    }

    final EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
    final int[] entityIds = new int[count];

    for (int i = 0; i < count; i++) {
      final EntityItem entity =
          new EntityItem(
              nmsPlayer.getWorld(),
              location.getX(),
              location.getY(),
              location.getZ(),
              CraftItemStack.asNMSCopy(item));

      entity.motX = randomEntityVelocity();
      entity.motY = randomEntityVelocity();
      entity.motZ = randomEntityVelocity();

      sendPacket(
          viewer,
          new PacketPlayOutSpawnEntity(entity, ENTITY_TYPE_IDS.get(org.bukkit.entity.Item.class)));
      sendPacket(
          viewer, new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));

      entityIds[i] = entity.getId();
    }

    scheduleEntityDestroy(plugin, viewer.getUniqueId(), duration, entityIds);
  }

  private static void scheduleEntityDestroy(
      Plugin plugin, UUID viewerUuid, Duration delay, int[] entityIds) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskLater(
            plugin,
            () -> {
              final Player viewer = plugin.getServer().getPlayer(viewerUuid);
              if (viewer != null) {
                sendPacket(viewer, new PacketPlayOutEntityDestroy(entityIds));
              }
            },
            delay.getSeconds() * 20);
  }

  /**
   * Replace a biome with another in the vanilla terrain generation system.
   *
   * @param from biome to replace
   * @param to biome to replace with
   * @throws NoSuchFieldException if replacement fails
   * @throws IllegalAccessException if replacement fails
   */
  public static void replaceBiome(BiomeBase from, BiomeBase to)
      throws NoSuchFieldException, IllegalAccessException {
    Field biomesField = BiomeBase.class.getDeclaredField("biomes");
    biomesField.setAccessible(true);

    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(biomesField, biomesField.getModifiers() & ~Modifier.FINAL);

    if (biomesField.get(null) instanceof BiomeBase[]) {
      BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
      biomes[from.id] = to;

      biomesField.set(null, biomes);
    }
  }

  /**
   * Get the raw minecraft {@link NBTTagCompound} representing a supplied entity.
   *
   * @param entity to get nbt data for
   * @return nbt compound from the entity
   */
  public static NBTTagCompound getNBT(org.bukkit.entity.Entity entity) {
    Entity nmsEntity = ((CraftEntity) entity).getHandle();

    NBTTagCompound tag = new NBTTagCompound();

    // writes entity's nbt data to OUR tag object
    nmsEntity.c(tag);
    return tag;
  }

  /**
   * Set the raw minecraft {@link NBTTagCompound} for an entity.
   *
   * @param entity to set the data for
   * @param compound to set
   */
  public static void setNBT(org.bukkit.entity.Entity entity, NBTTagCompound compound) {
    Entity nmsEntity = ((CraftEntity) entity).getHandle();

    ((EntityLiving) nmsEntity).a(compound);
  }

  private static net.minecraft.server.v1_8_R3.Packet destroyEntitiesPacket(int... entityIds) {
    return new net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy(entityIds);
  }

  /**
   * Play the default minecraft death animation (falling over) to mimic the death of the supplied
   * {@link Player}. The animation is sent to all entities that can see the player at the current
   * time, so this will not work if the player is already teleported or invisible.
   *
   * @param player to play the animation for
   */
  public static void playDeathAnimation(Player player) {
    EntityPlayer handle = ((CraftPlayer) player).getHandle();
    PacketPlayOutEntityMetadata packet =
        new PacketPlayOutEntityMetadata(handle.getId(), handle.getDataWatcher(), false);

    boolean replaced = false;

    // 3 - marks as a float field (see DataWatcher.class for a mapping).
    // 6 - the field number for the health data.
    // 0f - Makes the client think the entity is dead so it will render the death animation.
    DataWatcher.WatchableObject zeroHealth = new DataWatcher.WatchableObject(3, 6, 0f);

    // Can't use a PacketPlayOutEntityStatus because it doesn't actually work.
    // packet.b is the list of watchable objects in the packet.
    // Have to do this loop to weed out any health data already in the packet.
    // item.a() is the field number for the watcher.

    if (packet.b != null) {
      for (int i = 0; i < packet.b.size(); i++) {
        DataWatcher.WatchableObject item = packet.b.get(i);
        if (6 == item.a()) {
          packet.b.set(i, zeroHealth);
          replaced = true;
        }
      }
    }

    if (!replaced) {
      if (packet.b == null) {
        packet.b = Collections.singletonList(zeroHealth);
      } else {
        packet.b.add(zeroHealth);
      }
    }

    sendPacketToViewers(handle, packet);
  }

  public interface FakeEntity {

    /** @return id of this entity */
    int entityId();

    /** @return a bukkit entity relating to the NMS entity */
    org.bukkit.entity.Entity bukkitEntity();

    /** @return the NMS version of the entity */
    Entity entity();

    /**
     * Spawn the entity in.
     *
     * @param viewer who should see the action
     * @param location to spawn the entity at
     * @param velocity the entity should have on spawn
     */
    void spawn(Player viewer, Location location, Vector velocity);

    /**
     * Spawn the entity in.
     *
     * @param viewer who should see the action
     * @param location to spawn the entity at
     */
    default void spawn(Player viewer, Location location) {
      spawn(viewer, location, new Vector(0, 0, 0));
    }

    /**
     * Destroy the entity.
     *
     * @param viewer who should see the action
     */
    default void destroy(Player viewer) {
      sendPacket(viewer, destroyEntitiesPacket(entityId()));
    }

    /**
     * Move this entity in a relative direction.
     *
     * @param viewer who should see the action
     * @param delta to move the entity by
     * @param onGround if the entity is on the ground
     */
    default void move(Player viewer, Vector delta, boolean onGround) {
      sendPacket(viewer, moveEntityRelativePacket(entityId(), delta, onGround));
    }

    /**
     * Teleport this entity to a location
     *
     * @param viewer who should see the action
     * @param location to teleport the entity to
     */
    default void teleport(Player viewer, Location location) {
      sendPacket(viewer, teleportEntityPacket(entityId(), location));
    }

    /**
     * Set this entity to riding on another.
     *
     * @param viewer who should see the action
     * @param vehicle that this entity should ride on
     */
    default void ride(Player viewer, org.bukkit.entity.Entity vehicle) {
      sendPacket(viewer, setPassengerPacket(entity(), ((CraftEntity) vehicle).getHandle()));
    }

    /**
     * Mount an entity to this one.
     *
     * @param viewer who should see the action
     * @param rider to mount to this entity
     */
    default void mount(Player viewer, org.bukkit.entity.Entity rider) {
      sendPacket(viewer, setPassengerPacket(((CraftEntity) rider).getHandle(), entity()));
    }
  }

  private abstract static class FakeEntityImpl<T extends net.minecraft.server.v1_8_R3.Entity>
      implements FakeEntity {

    protected final T entity;

    FakeEntityImpl(T entity) {
      this.entity = entity;
    }

    @Override
    public org.bukkit.entity.Entity bukkitEntity() {
      return entity.getBukkitEntity();
    }

    @Override
    public Entity entity() {
      return entity;
    }

    @Override
    public int entityId() {
      return entity.getId();
    }
  }

  public static class FakeArmorStand extends FakeEntityImpl<EntityArmorStand> {

    /** @param world the stand is in */
    public FakeArmorStand(World world) {
      this(world, null);
    }

    /**
     * @param world the stand is in
     * @param name of the stand
     */
    public FakeArmorStand(World world, @Nullable String name) {
      this(world, name, true, true);
    }

    /**
     * @param world the stand is in
     * @param name of the stand
     * @param invisible attribute of the stand
     * @param small attribute of the stand
     */
    public FakeArmorStand(World world, @Nullable String name, boolean invisible, boolean small) {
      super(new EntityArmorStand(((CraftWorld) world).getHandle()));

      entity.setInvisible(invisible);
      entity.setSmall(small);
      entity.setBasePlate(false);
      entity.setArms(false);

      if (name != null) {
        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
      }
    }

    @Override
    public void spawn(Player viewer, Location location, Vector velocity) {
      sendPacket(
          viewer,
          spawnEntityPacket(
              ArmorStand.class, 0, entityId(), entity.getUniqueID(), location, velocity));
      sendPacket(viewer, entityMetadataPacket(entity, true));
    }
  }
}
