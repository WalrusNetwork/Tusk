package network.walrus.games.uhc.scenarios;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Data about the author of a UHC scenario.
 *
 * @author Austin Mayes
 */
public class ScenarioAuthorInfo {

  public static final ScenarioAuthorInfo UNKNOWN =
      new ScenarioAuthorInfo("Unknown", "https://walrus.gg");
  private final String name;
  private URL link;

  /**
   * @param name of the author
   * @param link to the author's profile
   */
  public ScenarioAuthorInfo(String name, String link) {
    this.name = name;
    try {
      this.link = new URL(link);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public String getName() {
    return name;
  }

  public URL getLink() {
    return link;
  }
}
