package network.walrus.utils.core.config;

/**
 * An attribute of a {@link Node}.
 *
 * <p>Implementations in configuration schemas which do not natively support attributes should take
 * special care to special-case certain strings as attribute identifiers and identify those as such,
 * instead of child nodes. This behaviour should be well documented.
 *
 * @author Austin Mayes
 */
public interface Attribute extends StringHolder {

  /**
   * The name of the attribute. In key-value configuration contexts, this would be referred to as
   * the key. Implementations should take care to assign this to the most appropriate value based on
   * the semantic layout of the specific language. We assume names are unique in the context of this
   * node, so implementations for languages which do not enforce this should do their best to ensure
   * uniqueness.
   */
  String name();
}
