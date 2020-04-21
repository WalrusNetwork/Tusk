package network.walrus.utils.bukkit.visual.renderable;

import java.util.UUID;
import network.walrus.utils.bukkit.visual.display.DisplayPane;

/**
 * Any renderable that can be displayed in game. The base class is extremely generic to allow for
 * implementations to add any custom data they need in order to display them.
 *
 * @author Austin Mayes
 */
public abstract class Renderable {

  private final String id;
  private final byte maxLines;

  /**
   * Constructor.
   *
   * @param id of the renderable
   * @param maxLines that the renderable takes up
   */
  public Renderable(String id, byte maxLines) {
    this.id = id;
    this.maxLines = maxLines;
  }

  /**
   * Constructor with 1 line.
   *
   * @param id of the renderable
   */
  public Renderable(String id) {
    this(id, (byte) 1);
  }

  /** Constructor with a random ID. */
  public Renderable() {
    this(UUID.randomUUID().toString());
  }

  /**
   * The maximum number of lines this renderable can possibly render at a time.
   *
   * @return maximum number of lines of the renderable
   */
  public byte maxLines() {
    return this.maxLines;
  }

  /**
   * The non-unique ID of this renderable. IDs for elements can be used to update all elements with
   * this ID across multiple panes, so uniqueness is not enforced. IDs must only be unique in the
   * same {@link DisplayPane}.
   *
   * @return the ID of the renderable
   */
  public String id() {
    return this.id;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null
        && obj instanceof Renderable
        && ((Renderable) obj).id().equalsIgnoreCase(this.id());
  }

  @Override
  public int hashCode() {
    return id().hashCode();
  }
}
