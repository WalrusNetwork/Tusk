package network.walrus.games.uhc.facets.groups;

import java.util.Optional;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser for UHC groups which bases all data off of the {@link UHCConfig}.
 *
 * @author Austin Mayes
 */
public class GroupsParser implements FacetParser<UHCGroupsManager> {

  @Override
  public Optional<UHCGroupsManager> parse(FacetHolder holder, Node<?> node)
      throws ParsingException {
    if (UHCManager.instance.getConfig().teamSize.get() > 1) {
      return Optional.of(new TeamsManager(holder));
    } else {
      return Optional.of(new SinglesManager(holder));
    }
  }
}
