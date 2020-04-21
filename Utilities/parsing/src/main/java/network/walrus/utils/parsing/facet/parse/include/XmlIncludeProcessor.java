package network.walrus.utils.parsing.facet.parse.include;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.library.WorldSource;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * An inclusion processor for XML documents.
 *
 * @author Avicus Network
 */
public class XmlIncludeProcessor implements IncludeProcessor {

  private final WorldSource source;
  private final Document document;
  private final SAXBuilder sax;

  /**
   * @param source of the file structure
   * @param document to be processed
   * @param sax builder used to read XML files
   */
  public XmlIncludeProcessor(WorldSource source, Document document, SAXBuilder sax) {
    this.source = source;
    this.document = document;
    this.sax = sax;
  }

  @Override
  public boolean shouldProcess() {
    return this.document.getRootElement().getChild("include") != null;
  }

  @Override
  public void process() throws ConfigurationParseException {
    List<Element> list = this.document.getRootElement().getChildren();
    List<Element> clone = new ArrayList<>(list);
    for (Element element : clone) {
      if (element.getName().equals("include")) {
        String src = element.getAttributeValue("src");
        boolean overwrite = element.getAttributeValue("overwrite", "false").equals("true");
        boolean local = element.getAttributeValue("local", "false").equals("true");

        InputStream file;
        try {
          if (local) {
            file = this.source.getFile(src);
          } else {
            file = this.source.getLibrary().getFileStream(src);
          }

          Document merge = this.sax.build(file);
          merge(merge, overwrite);
        } catch (IOException | JDOMException e) {
          throw new ConfigurationParseException("Failed to load included configuration file!", e);
        }

        this.document.getRootElement().removeContent(element);
      }
    }
  }

  private void merge(Document merge, boolean overwrite) {
    List<Content> content = merge.getRootElement().getContent();
    merge(this.document.getRootElement(), content, overwrite);
  }

  private void merge(Element current, List<Content> contents, boolean overwrite) {
    contents = new ArrayList<>(contents);

    for (Content content : contents) {
      if (content instanceof Element) {
        Element element = (Element) content;

        List<Element> children = current.getChildren();

        Element existing = null;
        for (Element child : children) {
          if (!child.getName().equals(element.getName())) {
            continue;
          }

          boolean sameAttributes = true;
          for (Attribute attribute : child.getAttributes()) {
            Attribute test = element.getAttribute(attribute.getName());

            if (test == null || !test.getValue().equals(attribute.getValue())) {
              sameAttributes = false;
              break;
            }
          }

          if (sameAttributes) {
            existing = child;
            break;
          }
        }

        if (overwrite) {
          if (existing != null) {
            current.removeChild(existing.getName());
          }
          current.addContent(element.detach());
        } else {
          // Add element if it isn't there, otherwise dive deeper
          if (existing == null) {
            current.addContent(element.detach());
          } else {
            merge(existing, element.getContent(), false);
          }
        }
      }
    }
  }
}
