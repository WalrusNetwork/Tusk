package network.walrus.utils.core.config.xml;

import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import org.jdom2.Attribute;
import org.jdom2.located.LocatedElement;

/**
 * An attribute from a JDOM document.
 *
 * @author Avicus Network
 */
public class XmlAttribute implements network.walrus.utils.core.config.Attribute {

  private final String name;
  private final Node node;
  private final Optional<String> value;

  /**
   * Constructor.
   *
   * @param node containing the attribute
   * @param name of the attribute to get data from
   */
  public XmlAttribute(Node node, String name) {
    value = getValue(node, name);
    this.node = node;
    this.name = name;
  }

  private static Optional<String> getValue(Node<LocatedElement> node, String name) {
    Attribute jdom = node.baseElement().getAttribute(name);
    return jdom == null ? Optional.empty() : Optional.of(jdom.getValue());
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public boolean isValuePresent() {
    return value.isPresent();
  }

  @Override
  public String asRequiredString() throws ParsingException {
    Optional<String> value = value();
    if (value.isPresent()) {
      return value.get();
    }
    throw new ParsingException(this.node, "Missing required attribute \"" + name + "\".");
  }

  @Override
  public Optional<String> value() {
    return this.value;
  }

  @Override
  public Node parent() {
    return this.node;
  }
}
