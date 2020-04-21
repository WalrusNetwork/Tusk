package network.walrus.utils.parsing.world;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import network.walrus.utils.parsing.world.library.WorldSource;
import org.apache.commons.io.FileUtils;
import org.bukkit.WorldCreator;

/**
 * An {@link WorldProvider} which provides worlds from {@link WorldSource}s.
 *
 * @author Austin Mayes
 */
public class MapSourceWorldProvider implements WorldProvider<GenericContainer> {

  private final WorldSource source;
  private final String name;
  private final AtomicBoolean loaded;
  private Consumer<WorldCreator> creationCallback;

  /**
   * Constructor.
   *
   * @param source to get the world from
   * @param name of the bukkit world
   */
  public MapSourceWorldProvider(WorldSource source, String name) {
    this.source = source;
    this.name = name;
    this.loaded = new AtomicBoolean(false);
  }

  @Override
  public String worldName() {
    return this.name;
  }

  @Override
  public void copyWorld(File path) throws IOException {
    FileUtils.copyDirectory(source(), path);
  }

  @Override
  public void postLoad(GenericContainer world) {
    this.loaded.set(true);
  }

  @Override
  public boolean loadFailed() {
    return !this.loaded.get();
  }

  @Override
  public File source() {
    return this.source.source();
  }

  @Override
  public void beforeCreate(WorldCreator creator) {
    if (creationCallback != null) {
      creationCallback.accept(creator);
    }
  }

  public void setCreationCallback(Consumer<WorldCreator> creationCallback) {
    this.creationCallback = creationCallback;
  }
}
