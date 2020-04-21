package network.walrus.games.core.external;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Class to represent a file that holds basic information about an external component.
 *
 * <p>component.ymls must contain:
 *
 * <p>name - Name of the component for ID lookup and logging. main - Main class of the component
 * which must extend {@link ExternalComponent}.
 *
 * @author Avicus Network
 */
public class ComponentDescriptionFile {

  private static final Yaml yaml = new Yaml(new SafeConstructor());
  private String main = null;
  private String name = null;

  /**
   * Constructor.
   *
   * @param stream containing the data to load
   * @throws Exception if the file contains errors
   */
  public ComponentDescriptionFile(final InputStream stream) throws Exception {
    loadMap(asMap(yaml.load(stream)));
  }

  /**
   * Constructor.
   *
   * @param reader containing the data to load
   * @throws Exception if the file contains errors
   */
  public ComponentDescriptionFile(final Reader reader) throws Exception {
    loadMap(asMap(yaml.load(reader)));
  }

  private void loadMap(Map<?, ?> map) throws Exception {
    try {
      name = map.get("name").toString();
    } catch (NullPointerException ex) {
      throw new Exception("name is not defined", ex);
    } catch (ClassCastException ex) {
      throw new Exception("name is of wrong type", ex);
    }

    try {
      main = map.get("main").toString();
    } catch (NullPointerException ex) {
      throw new Exception("main is not defined", ex);
    } catch (ClassCastException ex) {
      throw new Exception("main is of wrong type", ex);
    }
  }

  private Map<?, ?> asMap(Object object) throws Exception {
    if (object instanceof Map) {
      return (Map<?, ?>) object;
    }
    throw new Exception(object + " is not properly structured.");
  }

  public String getMain() {
    return main;
  }

  public String getName() {
    return name;
  }
}
