package network.walrus.games.uhc.facets.redditbans;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link RedditBansFacet}.
 *
 * @author Rafi Baum
 */
public class RedditBansConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(RedditBansFacet.class, (h) -> h instanceof UHCRound);
    bindFacetCommands(RedditBansCommands.class, RedditBansFacet.class);
  }
}
