package network.walrus.utils.bukkit.parse.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.ListParser;
import org.bukkit.Material;

/**
 * Parsers which convert {@link StringHolder}s to material matchers.
 *
 * @author Avicus Network
 */
public class MaterialMatcherParsers {

  /** Parser for {@link SingleMaterialMatcher}s. */
  public static class Single implements SimpleParser<SingleMaterialMatcher> {

    private final ListParser listParser;
    private final SimpleParser<Material> materialParser;
    private final SimpleParser<Byte> byteParser;

    /**
     * Constructor.
     *
     * @param listParser used to parse lists
     * @param materialParser used to parse materials
     * @param byteParser used to parse material data
     */
    public Single(
        ListParser listParser,
        SimpleParser<Material> materialParser,
        SimpleParser<Byte> byteParser) {
      this.listParser = listParser;
      this.materialParser = materialParser;
      this.byteParser = byteParser;
    }

    @Override
    public SingleMaterialMatcher parseRequired(StringHolder holder) throws ParsingException {
      List<StringHolder> list = listParser.parseRequiredList(holder, ":", true);

      if (list.size() > 2) {
        throw new ParsingException(holder.parent(), "Invalid material matcher.");
      }

      Material material = materialParser.parseRequired(list.get(0));
      Optional<Byte> data = Optional.empty();
      if (list.size() == 2) {
        data = Optional.of(byteParser.parseRequired(list.get(1)));
      }

      return new SingleMaterialMatcher(material, data);
    }
  }

  /** Parser for {@link MultiMaterialMatcher}s. */
  public static class Multi implements SimpleParser<MultiMaterialMatcher> {

    private final ListParser listParser;
    private final SimpleParser<SingleMaterialMatcher> matcherParser;

    /**
     * Constructor.
     *
     * @param listParser used to parse lists
     * @param matcherParser used to parse the individual matchers
     */
    public Multi(ListParser listParser, SimpleParser<SingleMaterialMatcher> matcherParser) {
      this.listParser = listParser;
      this.matcherParser = matcherParser;
    }

    @Override
    public MultiMaterialMatcher parseRequired(StringHolder holder) throws ParsingException {
      List<StringHolder> matcherHolders = listParser.parseRequiredList(holder, ",", true);
      List<SingleMaterialMatcher> matchers = new ArrayList<>();
      for (StringHolder matcherHolder : matcherHolders) {
        SingleMaterialMatcher singleMaterialMatcher = matcherParser.parseRequired(matcherHolder);
        matchers.add(singleMaterialMatcher);
      }

      return new MultiMaterialMatcher(matchers);
    }
  }
}
