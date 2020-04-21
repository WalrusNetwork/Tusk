package network.walrus.utils.core.config;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * A {@link StringHolder} which is not linked to a specific configuration format. This is used
 * internally to allow the creation of usable holders without the need for a base element.
 *
 * @author Austin Mayes
 */
public class GenericStringHolder implements StringHolder {

  private final String s;
  private final Node parent;

  /**
   * Constructor.
   *
   * @param s value of the holder
   * @param parent of the holder, if there is one
   */
  public GenericStringHolder(String s, Node parent) {
    this.s = s;
    this.parent = parent;
  }

  @Override
  public boolean isValuePresent() {
    return s != null && !s.isEmpty();
  }

  @Override
  public String asRequiredString() throws ParsingException {
    return s;
  }

  @Override
  public Optional<String> value() {
    return Optional.ofNullable(s);
  }

  @Override
  public Node parent() {
    if (parent != null) {
      return parent;
    }

    return new Node() {
      @Nonnull
      @Override
      public String name() {
        return "generic string holder";
      }

      @Nonnull
      @Override
      public Attribute attribute(String name) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasAttribute(String name) {
        return false;
      }

      @Override
      public boolean hasText() {
        return false;
      }

      @Nonnull
      @Override
      public List<Attribute> attributes() {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public Optional<Node> parent() {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public Text text() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void inheritAttributes(String parentName) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void inheritAttributes(String parentName, List ignored) {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public List<Node> descendants() {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public List<Node> descendants(String name) {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public List<Node> children() {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public List<Node> children(String name) {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public Optional<Node> child(String name) {
        throw new UnsupportedOperationException();
      }

      @Nonnull
      @Override
      public Node childRequired(String name) throws ParsingException {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasChild(String name) {
        return false;
      }

      @Override
      public int startLine() {
        return 0;
      }

      @Override
      public int endLine() {
        return 0;
      }

      @Override
      public int column() {
        return 0;
      }

      @Override
      public int size() {
        return 0;
      }

      @Nonnull
      @Override
      public String description() {
        return "A fake element";
      }

      @Override
      public Object baseElement() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
