package network.walrus.games.uhc;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import network.walrus.utils.parsing.world.PlayerContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Container containing the collection of worlds players can be in during a UHC round.
 *
 * @author Austin Mayes
 */
public class UHCWorld implements PlayerContainer {

  private final World overWorld;
  private final Optional<World> nether;
  private final Optional<World> end;
  private final List<World> worlds;

  /**
   * @param overWorld the main world
   * @param nether the nether world, if is is enabled
   * @param end the end world, if is is enabled
   */
  UHCWorld(World overWorld, Optional<World> nether, Optional<World> end) {
    this.overWorld = overWorld;
    this.nether = nether;
    this.end = end;
    this.worlds = Lists.newArrayList();
    worlds.add(overWorld);
    nether.ifPresent(worlds::add);
    end.ifPresent(worlds::add);
  }

  @Override
  public boolean isInside(World world) {
    return this.worlds.contains(world);
  }

  @Override
  public World mainWorld() {
    return this.overWorld;
  }

  @Override
  public List<Player> players() {
    List<Player> players = new ArrayList<>(this.overWorld.getPlayers());
    nether.ifPresent(w -> players.addAll(w.getPlayers()));
    end.ifPresent(w -> players.addAll(w.getPlayers()));
    return players;
  }

  @Override
  public void actOnAllWorlds(Consumer<World> function) {
    for (World world : worlds) {
      function.accept(world);
    }
  }
}
