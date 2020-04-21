package network.walrus.utils.core.config;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

/**
 * A node in a general configuration document. Nodes contain attributes, text, and can have
 * children. These should be used at all cost when dealing with pulling data from user configuration
 * files, in an effort to support multiple configuration languages in the future. These are parsed
 * "as is" and all further parsing should assume no validation unless otherwise noted in the
 * specific parsing environment.
 *
 * @param <E> the base type of node
 * @author Austin Mayes
 */
public interface Node<E> {

  /**
   * Convert the start, end, and column location of a node into a human-readable location. If any of
   * the values cannot be determined at call time, they can be set to any value below {@code 0}.
   *
   * @param startLine of the node being described
   * @param endLine of the node being described
   * @param column of the node being described
   * @return string representation of the location of the node in the document
   */
  static Optional<String> describeLocation(int startLine, int endLine, int column) {
    if (startLine > 0) {
      if (endLine > 0 && endLine != startLine) {
        return Optional.of("line " + startLine + " to " + endLine);
      }

      if (column > 0) {
        return Optional.of("line " + startLine + ", column " + column);
      }

      return Optional.of("line " + startLine);
    }

    return Optional.empty();
  }

  /**
   * Reduce a known and optional value of type {@link R} and combine them using the supplied
   * function.
   *
   * @param identity which will always be present
   * @param optional which might not exist
   * @param combiner used to combine the two values
   * @param <T> type of argument
   * @param <R> type of return value
   */
  static <T, R> R reduce(
      R identity, Optional<T> optional, BiFunction<? super R, ? super T, ? extends R> combiner) {
    return optional.<R>map(value -> combiner.apply(identity, value)).orElse(identity);
  }

  /**
   * The name of the element.
   *
   * <p>In key-value configuration contexts, this would be referred to as the key. Implementations
   * should take care to assign this to the most appropriate value based on the semantic layout of
   * the specific language. We assume names are unique in the context of this node, so
   * implementations for languages which do not enforce this should do their best to ensure
   * uniqueness.
   */
  @Nonnull
  String name();

  /**
   * A simple description of the node to allow an end user to distinguish this object from another
   * similar one in the document.
   *
   * <p>All location information is supplied by {@link #describeLocation(int, int, int)}, so it is
   * not needed here.
   */
  @Nonnull
  String description();

  /**
   * Get an {@link Attribute} by name. This will ALWAYS return an attribute, regardless of if on
   * exists. Callers should use {@link Attribute#isValuePresent()} OR {@link #hasAttribute(String)}
   * to check for data presence.
   *
   * @param name to search for
   */
  @Nonnull
  Attribute attribute(String name);

  /**
   * Check if the node has a specific attribute.
   *
   * @param name to search for
   */
  boolean hasAttribute(String name);

  /** Check if this node has any text. */
  boolean hasText();

  /** Get all of the attributes that this node contains. This is never null, only empty. */
  @Nonnull
  List<Attribute> attributes();

  /**
   * Get the parent of this node, if one exists. In hierarchical configuration structures, the only
   * node without a parent should be the root.
   */
  @Nonnull
  Optional<Node<E>> parent();

  /**
   * Get the {@link Text} of the node. This will ALWAYS return an object, regardless of if on
   * exists. Callers should use {@link Text#isValuePresent()} OR {@link #hasText()} to check for
   * data presence.
   */
  @Nonnull
  Text text();

  /**
   * See {@link #inheritAttributes(String, List)}.
   *
   * @param parentName to inherit attributes from
   */
  void inheritAttributes(String parentName);

  /**
   * Inherit attributes from the specified parent node, combining them with the current ones.
   *
   * <p>Implementations should use the "ours > theirs" approach, and should favor our attributes
   * over the parent if we both have the same named attribute. This allows children to only override
   * a certain attribute without having to copy all of the same from the parents.
   *
   * <p>If this node has no parent, nothing will happen.
   *
   * @param parentName to inherit attributes from
   * @param ignored attributes that will not be inherited
   */
  void inheritAttributes(String parentName, List<String> ignored);

  /**
   * Get all descendants of this node.
   *
   * <p>This should not be confused with {@link #children()} as this runs in a nested fashion and
   * returns all nodes that have this one somewhere in their inheritance tree.
   */
  @Nonnull
  List<Node<E>> descendants();

  /**
   * Filtered version of {@link #descendants()} that uses {@link #name()} as a comparator.
   *
   * @param name to filter by
   */
  @Nonnull
  List<Node<E>> descendants(String name);

  /** Get only the direct children of this node. */
  @Nonnull
  List<Node<E>> children();

  /**
   * Filtered version of {@link #children()} that uses {@link #name()} as a comparator.
   *
   * @param name to filter by
   */
  @Nonnull
  List<Node<E>> children(String name);

  /**
   * Find a child by a specific name, or empty if none exists.
   *
   * <p>If the node has more than one child matching this name, the first one will be returned.
   *
   * @param name to search for
   */
  @Nonnull
  Optional<Node<E>> child(String name);

  /**
   * Convenience method for {@link #child(String)} that will throw an error if no child is present.
   * If the node has more than one child matching this name, the first one will be returned.
   *
   * @param name to search for
   * @throws ParsingException if the node has no child matching the specified name
   */
  @Nonnull
  Node<E> childRequired(String name) throws ParsingException;

  /**
   * Check if the node has a child matching the specified name.
   *
   * @param name to search for
   */
  boolean hasChild(String name);

  /**
   * The line number that this node begins at in the document.
   *
   * <p>This is used strictly for {@link ParsingException} to provide a useful error message that
   * allows users to easily identify the problem.
   */
  int startLine();

  /**
   * The line number that this node ends at in the document.
   *
   * <p>This is used strictly for {@link ParsingException} to provide a useful error message that
   * allows users to easily identify the problem.
   */
  int endLine();

  /** The column in the document where this node begins. */
  int column();

  /** The content size of this node before any code alterations. */
  int size();

  /**
   * Get the base element which this node was created from. This should never be null, unless this
   * node was created by tests.
   */
  E baseElement();

  /** @return {@link #describeLocation(int, int, int)} with the values from this node */
  default Optional<String> describeLocation() {
    return describeLocation(startLine(), endLine(), column());
  }

  /** @return reduced form of {@link #description()} and {@link #describeLocation()} */
  default String describeWithLocation() {
    return reduce(description(), describeLocation(), (d, l) -> d + " @ " + l);
  }
}
