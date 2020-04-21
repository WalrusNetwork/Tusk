package network.walrus.utils.parsing.world;

import java.io.File;
import java.io.IOException;

/**
 * A world provider that returns a pre-defined world.
 *
 * @param <W> type of world this provider provides
 * @author Austin Mayes
 */
public class ActionlessWorldProvider<W extends PlayerContainer> implements WorldProvider<W> {

  private final W world;

  /** @param world to provide */
  public ActionlessWorldProvider(W world) {
    this.world = world;
  }

  @Override
  public String worldName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void copyWorld(File path) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void postLoad(W world) {
    // Nothing!
  }

  @Override
  public File source() {
    throw new UnsupportedOperationException();
  }

  @Override
  public W load() {
    return world;
  }
}
