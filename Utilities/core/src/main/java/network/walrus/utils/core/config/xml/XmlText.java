package network.walrus.utils.core.config.xml;

import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.Text;
import org.jdom2.located.LocatedElement;

/**
 * Text inside of an {@link XmlElement}.
 *
 * @author Avicus Network
 */
public class XmlText implements Text {

  private final Node node;
  private final Optional<String> value;

  /**
   * Constructor.
   *
   * @param node that contains the text
   */
  public XmlText(Node node) {
    this.node = node;
    value = getValue(node);
  }

  private static Optional<String> getValue(Node<LocatedElement> node) {
    String value = node.baseElement().getTextTrim();
    if (value.length() == 0) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  @Override
  public String asRequiredString() throws ParsingException {
    Optional<String> value = value();
    if (value.isPresent()) {
      return value.get();
    }
    throw new ParsingException(this.node, "Missing required value.");
  }

  @Override
  public Optional<String> value() {
    return value;
  }

  @Override
  public boolean isValuePresent() {
    return value.isPresent();
  }

  public Node parent() {
    return this.node;
  }
}
