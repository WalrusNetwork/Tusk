package network.walrus.ubiquitous.bukkit.boss;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * A legacy boss bar.
 *
 * <p>
 *
 * <p>A legacy boss bar is used for 1.8 clients.
 *
 * @author kashike
 */
public class LegacyBossBar extends BossBar {

  /** A fake id for sending a {@link EntityType#WITHER} to a client. */
  private static final int FAKE_ENTITY_ID;

  static {
    try {
      final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
      final Field field =
          Class.forName("net.minecraft.server." + version + ".Entity")
              .getDeclaredField("entityCount");
      field.setAccessible(true);
      FAKE_ENTITY_ID = field.getInt(null);
      field.set(null, FAKE_ENTITY_ID + 1);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  /** The boss bar manager. */
  private BossBarManager manager;
  /** The player who is viewing this boss bar. */
  private Player player;
  /** If this boss bar has been sent to the client. */
  private boolean sentToClient;
  /** A cached version of {@link #name} in legacy text format. */
  @Nullable private String cachedLegacyText;

  /**
   * Constructs a new legacy boss bar.
   *
   * @param manager the boss bar manager
   * @param player the player who is viewing this boss bar
   */
  LegacyBossBar(BossBarManager manager, Player player) {
    this.manager = manager;
    this.player = player;
    this.manager.context.add(this.player, this);
  }

  @Override
  public void destroy() {
    this.despawn();
    this.manager.legacyUpdateSubscribers.remove(this);
    this.manager.context.remove(this.player, this);
    this.manager = null;
    this.player = null;
  }

  @Override
  public LegacyBossBar setName(BaseComponent[] name) {
    if (name != this.name) {
      super.setName(name);
      this.cachedLegacyText = null;
      this.respawn();
    }

    return this;
  }

  @Override
  public LegacyBossBar setPercent(float percent) {
    if (percent != this.percent) {
      super.setPercent(percent);
      this.respawn();
    }

    return this;
  }

  @Override
  public LegacyBossBar setColor(BossBarColor color) {
    // not supported
    super.setColor(color);
    return this;
  }

  @Override
  public LegacyBossBar setOverlay(BossBarOverlay overlay) {
    // not supported
    super.setOverlay(overlay);
    return this;
  }

  @Override
  public LegacyBossBar setDarkenSky(boolean darkenSky) {
    // not supported
    super.setDarkenSky(darkenSky);
    return this;
  }

  @Override
  public LegacyBossBar setPlayEndBossMusic(boolean playEndBossMusic) {
    // not supported
    super.setPlayEndBossMusic(playEndBossMusic);
    return this;
  }

  @Override
  public LegacyBossBar setCreateFog(boolean createFog) {
    // not supported
    super.setCreateFog(createFog);
    return this;
  }

  @Override
  public LegacyBossBar setVisible(boolean visible) {
    if (visible != this.visible) {
      super.setVisible(visible);

      if (visible) {
        this.spawn();
      } else {
        this.despawn();
      }
    }

    return this;
  }

  /** Respawn the fake entity on the client with updated information. */
  void respawn() {
    this.manager.context.refresh(this.player, this);
  }

  /** Spawn a fake entity on the client used to render the boss bar. */
  void spawn() {
    // Reposition
    final Location eye = this.player.getEyeLocation().clone();
    final Location location = eye.add(eye.getDirection().normalize().multiply(50));

    final PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    final StructureModifier<Object> modifier = packet.getModifier();
    modifier.write(0, FAKE_ENTITY_ID); // entity id
    modifier.write(1, (byte) EntityType.WITHER.getTypeId());
    modifier.write(2, location.getBlockX() * 32);
    modifier.write(3, location.getBlockY() * 32);
    modifier.write(4, location.getBlockZ() * 32);
    packet.getDataWatcherModifier().write(0, this.asDataWatcher());

    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, packet, false);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    this.sentToClient = true;
    this.manager.legacyUpdateSubscribers.add(this);
  }

  /**
   * Create a data watcher filled with information used to render the boss bar on the client.
   *
   * @return the data watcher
   */
  private WrappedDataWatcher asDataWatcher() {
    if (this.cachedLegacyText == null) {
      this.cachedLegacyText = BaseComponent.toLegacyText(this.name);
    }

    final WrappedDataWatcher watcher = new WrappedDataWatcher();
    watcher.setObject(0, (byte) 0x20); // invisible
    watcher.setObject(2, this.cachedLegacyText); // custom name
    watcher.setObject(6, (float) Math.floor(this.percent * 300.0f), true); // health
    watcher.setObject(10, this.cachedLegacyText); //
    watcher.setObject(20, 881); // invulnerable time
    return watcher;
  }

  /** Removes the fake entity from the client. */
  void despawn() {
    if (!this.sentToClient) {
      return;
    }

    final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    packet.getIntegerArrays().write(0, new int[] {FAKE_ENTITY_ID});

    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, packet, false);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
