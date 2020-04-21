package network.walrus.utils.parsing.world;

import java.util.List;
import java.util.function.Consumer;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * A {@link PlayerContainer} which handles only one world.
 *
 * @author Avicus Network
 */
public class GenericContainer implements PlayerContainer {

  private final World world;

  /**
   * Constructor.
   *
   * @param world this container is for
   */
  public GenericContainer(World world) {
    this.world = world;
  }

  @Override
  public World mainWorld() {
    return world;
  }

  @Override
  public List<Player> players() {
    return world.getPlayers();
  }

  @Override
  public void actOnAllWorlds(Consumer<World> function) {
    function.accept(world);
  }

  @Override
  public boolean isInside(World world) {
    return world.equals(this.world);
  }
}
