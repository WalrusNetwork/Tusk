package network.walrus.utils.core.config.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import network.walrus.utils.core.config.Attribute;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.Text;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.located.LocatedElement;

/**
 * This is a wrapper of jdom2 Element that utilizes Java 8 Optional.
 *
 * @author Avicus Network
 */
public class XmlElement implements Node<LocatedElement> {

  private final LocatedElement jdomElement;

  /**
   * Constructor.
   *
   * @param jdomElement used to create this element
   */
  public XmlElement(Element jdomElement) {
    if (!(jdomElement instanceof LocatedElement)) {
      throw new IllegalArgumentException("Element must be a LocatedElement");
    }
    this.jdomElement = (LocatedElement) jdomElement;
  }

  private static Node<LocatedElement> fromJdom(org.jdom2.Element element) {
    return new XmlElement(element);
  }

  private static List<Node<LocatedElement>> fromJdom(List<org.jdom2.Element> elements) {
    List<Node<LocatedElement>> list = new ArrayList<>();
    for (Element element : elements) {
      Node<LocatedElement> fromJdom = fromJdom(element);
      list.add(fromJdom);
    }
    return list;
  }

  @Override
  @Nonnull
  public String name() {
    return this.jdomElement.getName();
  }

  @Override
  @Nonnull
  public Attribute attribute(String name) {
    return new XmlAttribute(this, name);
  }

  @Override
  public boolean hasAttribute(String name) {
    return this.jdomElement.getAttribute(name) != null;
  }

  @Override
  public boolean hasText() {
    return this.text().isValuePresent();
  }

  @Override
  @Nonnull
  public List<Attribute> attributes() {
    List<Attribute> list = new ArrayList<>();
    for (org.jdom2.Attribute attribute : this.jdomElement.getAttributes()) {
      XmlAttribute xmlAttribute = new XmlAttribute(this, attribute.getName());
      list.add(xmlAttribute);
    }
    return list;
  }

  @Override
  @Nonnull
  public Optional<Node<LocatedElement>> parent() {
    return Optional.of(new XmlElement(this.jdomElement.getParentElement()));
  }

  @Override
  @Nonnull
  public Text text() {
    return new XmlText(this);
  }

  @Override
  public void inheritAttributes(String parentName) {
    if (!parent().isPresent()) {
      return;
    }

    Optional<Node<LocatedElement>> parent = parent();
    while (parent.isPresent() && parent.get().name().equals(parentName)) {
      for (org.jdom2.Attribute attribute : parent.get().baseElement().getAttributes()) {
        if (!attribute(attribute.getName()).isValuePresent()) {
          baseElement().setAttribute(attribute.getName(), attribute.getValue());
        }
      }
      parent = parent.get().parent();
    }
  }

  @Override
  public void inheritAttributes(String parentName, List<String> ignored) {
    if (!parent().isPresent()) {
      return;
    }

    Optional<Node<LocatedElement>> parent = parent();
    while (parent.isPresent() && parent.get().name().equals(parentName)) {
      for (org.jdom2.Attribute a : parent.get().baseElement().getAttributes()) {
        if (!ignored.contains(a.getName())) {
          if (!attribute(a.getName()).isValuePresent()) {
            baseElement().setAttribute(a.getName(), a.getValue());
          }
        }
      }
      parent = parent.get().parent();
    }
  }

  @Override
  @Nonnull
  public List<Node<LocatedElement>> descendants() {
    List<Element> elements = new ArrayList<>();
    for (Content content : this.jdomElement.getDescendants()) {
      if (content instanceof Element) {
        elements.add((Element) content);
      }
    }
    return fromJdom(elements);
  }

  @Override
  @Nonnull
  public List<Node<LocatedElement>> descendants(String name) {
    List<Node<LocatedElement>> nodes = descendants();
    Iterator<Node<LocatedElement>> iterator = nodes.iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().name().equals(name)) {
        iterator.remove();
      }
    }
    return nodes;
  }

  @Override
  @Nonnull
  public List<Node<LocatedElement>> children() {
    return fromJdom(this.jdomElement.getChildren());
  }

  @Override
  @Nonnull
  public List<Node<LocatedElement>> children(String name) {
    return fromJdom(this.jdomElement.getChildren(name));
  }

  @Override
  @Nonnull
  public Optional<Node<LocatedElement>> child(String name) {
    if (hasChild(name)) {
      return Optional.of(fromJdom(this.jdomElement.getChild(name)));
    }
    return Optional.empty();
  }

  @Override
  @Nonnull
  public Node<LocatedElement> childRequired(String name) throws ParsingException {
    Optional<Node<LocatedElement>> child = child(name);
    if (child.isPresent()) {
      return child.get();
    }
    throw new ParsingException(this, "Missing required child \"" + name + "\".");
  }

  @Override
  public boolean hasChild(String name) {
    return this.jdomElement.getChild(name) != null;
  }

  @Override
  public int column() {
    return this.jdomElement.getColumn();
  }

  @Override
  public int size() {
    return this.jdomElement.getContentSize();
  }

  public String toString() {
    return this.jdomElement.toString();
  }

  @Override
  public LocatedElement baseElement() {
    return jdomElement;
  }

  @Nonnull
  @Override
  public String description() {
    return "Element " + name();
  }

  @Override
  public int startLine() {
    return this.jdomElement.getLine();
  }

  @Override
  public int endLine() {
    return this.jdomElement.getLine();
  }
}
