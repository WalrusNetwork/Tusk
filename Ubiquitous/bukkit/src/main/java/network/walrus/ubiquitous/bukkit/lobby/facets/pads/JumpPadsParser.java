package network.walrus.ubiquitous.bukkit.lobby.facets.pads;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.util.Vector;

/**
 * Parses the {@link JumpPadsManager}.
 *
 * @author Austin Mayes
 */
public class JumpPadsParser implements FacetParser<JumpPadsManager> {

  @Override
  public Optional<JumpPadsManager> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<? extends Node<?>> padNodes = node.children("jump-pads");
    if (padNodes.isEmpty()) return Optional.empty();

    Set<JumpPad> pads = Sets.newHashSet();
    for (Node<?> padNode : padNodes) {
      for (Node<?> child : padNode.children()) {
        BoundedRegion region =
            holder
                .getRegistry()
                .get(BoundedRegion.class, child.attribute("region").asRequiredString(), true)
                .get();
        Vector velocity =
            BukkitParserRegistry.vectorParser().parseRequired(child.attribute("velocity"));
        PadType type =
            BukkitParserRegistry.ofEnum(PadType.class)
                .parse(child.attribute("type"))
                .orElse(PadType.NORMAL);
        pads.add(new JumpPad(region, velocity, type.permission, type.errorMessage));
      }
    }
    return Optional.of(new JumpPadsManager(pads));
  }

  public enum PadType {
    NORMAL(Optional.empty(), Optional.empty()),
    PREMIUM(
        Optional.of("lobby.pads.use.premium"),
        Optional.of(
            UbiquitousMessages.JUMP_PAD_USE_ERROR.with(
                new UnlocalizedText("have a premium rank")))),
    TOURNAMENT_WINNER(
        Optional.of("lobby.pads.use.tourney-winner"),
        Optional.of(
            UbiquitousMessages.JUMP_PAD_USE_ERROR.with(
                new UnlocalizedText("be a tournament winner")))),
    STAFF(
        Optional.of("lobby.pads.use.staff"),
        Optional.of(UbiquitousMessages.JUMP_PAD_USE_ERROR.with(new UnlocalizedText("be staff"))));

    private final Optional<String> permission;
    private final Optional<Localizable> errorMessage;

    PadType(Optional<String> permission, Optional<Localizable> errorMessage) {
      this.permission = permission;
      this.errorMessage = errorMessage;
    }
  }
}
