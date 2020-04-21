package network.walrus.utils.parsing.world;

import java.util.List;
import java.util.function.Consumer;
import network.walrus.common.text.PersonalizedComponent;
import network.walrus.utils.bukkit.sound.ConfiguredSound;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * A wrapper class used to manage {@link World}s which {@link org.bukkit.entity.Player}s can be
 * inside of. This is used to add multi-world support to multiple places and to provide a generic
 * way to get base access to a bukkit world.
 *
 * @author Austin Mayes
 */
public interface PlayerContainer {

  /** @return the main world where players will initially spawn */
  World mainWorld();

  /** @return all players inside of this container */
  List<Player> players();

  /**
   * Determine if this container contains a certain world.
   *
   * @param world to check
   * @return if this container contains the world
   */
  boolean isInside(World world);

  /** @see #isInside(World). */
  default boolean isInside(Location location) {
    return this.isInside(location.getWorld());
  }

  /** @see #isInside(World). */
  default boolean isInside(Block block) {
    return this.isInside(block.getLocation());
  }

  /** @see #isInside(World). */
  default boolean isInside(BlockState block) {
    return this.isInside(block.getLocation());
  }

  /** @see #isInside(World). */
  default boolean isInside(Entity entity) {
    return this.isInside(entity.getLocation());
  }

  /** @see #isInside(World). */
  default boolean isInside(Chunk chunk) {
    return this.isInside(chunk.getWorld());
  }

  /**
   * Send a message to all players in this container.
   *
   * @param component to broadcast
   */
  default void broadcast(PersonalizedComponent component) {
    for (Player p : players()) {
      p.sendMessage(component);
    }
  }

  /**
   * Play a sound for all players in this container.
   *
   * @param sound to play
   */
  default void broadcast(ConfiguredSound sound) {
    sound.play(players());
  }

  /**
   * Execute a {@link Consumer} on each world in this container.
   *
   * @param function to perform on each world
   */
  void actOnAllWorlds(Consumer<World> function);
}
