package network.walrus.utils.parsing.world;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/**
 * Used to provide Worlds for use in various places.
 *
 * @param <W> type of world this provider provides
 * @author Austin Mayes
 */
public interface WorldProvider<W extends PlayerContainer> {

  /** The name of the world. */
  String worldName();

  /** The file where this world will be copied to before it is loaded into Bukkit. */
  default File destination() {
    return new File(worldName());
  }

  /**
   * Load the world into the Bukkit server world system. This will copy files from the {@link
   * #source()} to the {@link #destination()} as well as set some global world options.
   *
   * @return the loaded world wrapped in a {@link W} implementation.
   */
  default W load() {
    try {
      WorldCreator creator = new WorldCreator(worldName());
      creator.generator(new NullChunkGenerator());
      creator.type(WorldType.FLAT);
      copyWorld(destination());
      beforeCreate(creator);
      World loaded = creator.createWorld();
      loaded.setAutoSave(false);
      loaded.setDifficulty(Bukkit.getServer().getWorlds().get(0).getDifficulty());
      W wrapped = wrap(loaded);
      postLoad(wrapped);
      return wrapped;
    } catch (Exception e) {
      failed(e);
      return null;
    }
  }

  /**
   * Copy files from this source to a folder.
   *
   * @param path path to copy the files to.
   * @throws IOException if the files cannot be copied
   */
  void copyWorld(File path) throws IOException;

  /**
   * Wrap a bukkit world in a custom {@link W} implementation to allow for further capabilities.
   * This is only called once per world.
   *
   * @param bukkit raw loaded world
   * @return a wrapped world
   */
  default W wrap(World bukkit) {
    return (W) new GenericContainer(bukkit);
  }

  /**
   * Called after the world is loaded and before any players are inside of it.
   *
   * @param world which has been loaded
   */
  void postLoad(W world);

  /**
   * Called if loading of the world fails.
   *
   * @param e which was thrown
   */
  default void failed(Exception e) {
    e.printStackTrace();
  }

  /** @return if world loading failed for any reason */
  default boolean loadFailed() {
    return false;
  }

  /** Source where the world files should be copied from. */
  File source();

  /**
   * Callback which is called directly before the provided {@link WorldCreator} is used to create
   * the world.
   *
   * @param creator used to create the world
   */
  default void beforeCreate(WorldCreator creator) {}
}
