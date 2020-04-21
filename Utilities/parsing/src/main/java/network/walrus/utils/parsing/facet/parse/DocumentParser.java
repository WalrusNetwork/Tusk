package network.walrus.utils.parsing.facet.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.holder.FacetHolderParser;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;

/**
 * The global handler responsible for handling the end-to-end parsing chain for each {@link
 * FacetHolder}. This handles all errors during parsing, and propagates them to the appropriate
 * places.
 *
 * @param <H> type of holder being parsed
 * @author Austin Mayes
 */
public class DocumentParser<H extends FacetHolder> {

  private static final Map<
          Class<? extends FacetParser>, List<Pair<FacetParser<?>, Predicate<FacetHolder>>>>
      parsers = Maps.newLinkedHashMap();
  private final H holder;
  private final FacetHolderParser<H> parser;
  private final Node parent;
  private final Logger logger;
  private final List<FacetWithParser> parsed = Lists.newArrayList();

  /**
   * Constructor.
   *
   * @param holder that this parser will be parsing
   * @param parser used to handle parsing callbacks
   * @param logger to log errors and benchmarking data to
   */
  public DocumentParser(H holder, FacetHolderParser<H> parser, Logger logger) {
    this.holder = holder;
    this.parser = parser;
    this.parent = holder.getSource().parent();
    this.logger = logger;
  }

  /**
   * Register a specific facet parser with the main document parser that can be used during document
   * evaluation.
   *
   * @param parser to register
   */
  public static void registerParser(FacetParser parser) {
    registerParser(parser, (h) -> true);
  }

  /**
   * Register a specific facet parser with the main document parser that can be used during document
   * evaluation.
   *
   * @param parser to register
   * @param usePredicate which is checked for each {@link FacetHolder} to determine if the parser
   *     should be used
   */
  public static void registerParser(FacetParser<?> parser, Predicate<FacetHolder> usePredicate) {
    if (!parsers.containsKey(parser.getClass())) {
      parsers.put(parser.getClass(), Lists.newArrayList());
    }

    parsers.get(parser.getClass()).add(Pair.of(parser, usePredicate));
  }

  /**
   * Register a specific facet parser with the main document parser that can be used during document
   * evaluation.
   *
   * @param parser to register
   */
  public static void registerParser(Class<? extends FacetParser> parser) {
    registerParser(parser, (h) -> true);
  }

  /**
   * Register a specific facet parser with the main document parser that can be used during document
   * evaluation.
   *
   * @param parser to register
   * @param usePredicate which is checked for each {@link FacetHolder} to determine if the parser
   *     should be used
   */
  public static void registerParser(
      Class<? extends FacetParser> parser, Predicate<FacetHolder> usePredicate) {
    try {
      registerParser(parser.newInstance(), usePredicate);
    } catch (InstantiationException | IllegalAccessException e) {
      Bukkit.getLogger().log(Level.SEVERE, "Failed to add parser factory!", e);
    }
  }

  /**
   * Find a parser instance using the class which declared it. If a parser is not found matching the
   * supplied class, an exception will be thrown.
   *
   * @param clazz used to find the parser by
   * @param <F> type of parser being queried
   * @return parser instance matching the supplied class
   */
  public static <F extends FacetParser> F getParser(Class<F> clazz) {
    for (Entry<Class<? extends FacetParser>, List<Pair<FacetParser<?>, Predicate<FacetHolder>>>>
        parser : parsers.entrySet()) {
      Pair<FacetParser<?>, Predicate<FacetHolder>> found = null;
      for (Pair<FacetParser<?>, Predicate<FacetHolder>> c : parser.getValue()) {
        if (c.getKey().getClass().isAssignableFrom(clazz)) {
          found = c;
          break;
        }
      }
      if (found == null) continue;

      return (F) found.getKey();
    }

    throw new RuntimeException(
        "Unable to find facet parser when searching for " + clazz.getSimpleName());
  }

  /**
   * Parse all {@link Facet}s using {@link FacetParser} and add them to the list of fully completed
   * facets. This will also handle pre and post parsing callbacks from the {@link
   * FacetHolderParser}.
   *
   * @throws ParsingException if the user provided invalid data
   */
  public void parse() throws ParsingException {
    try {
      final long start = System.nanoTime();
      logger.fine("Beginning parsing of " + holder.getSource().source().getName() + "...");

      logger.fine("Running pre parse...");
      parser.preParse(this.holder, this.parent);

      logger.fine("Running main parse...");
      parseInternal();

      logger.fine("Running post parse...");
      parser.postParse(this.holder, this.parsed);

      final long end = System.nanoTime();
      final long durr = ((end - start) / 1000000);
      if (durr > 10000) {
        logger.severe("Parsing took " + durr + "ms! (Over 10 seconds)");
      } else if (durr > 5000) {
        logger.warning("Parsing took " + durr + "ms! (Over 5 seconds)");
      } else {
        logger.fine("Parsing Finished! Took: " + durr + "ms");
      }
    } catch (Exception e) {
      if (e instanceof ParsingException) {
        throw e;
      } else {
        throw new ParsingException(e.getMessage(), e);
      }
    }
  }

  private void parseInternal() {
    for (List<Pair<FacetParser<?>, Predicate<FacetHolder>>> baseList : this.parsers.values()) {
      for (Pair<FacetParser<?>, Predicate<FacetHolder>> parserPair : baseList) {
        if (!parserPair.getValue().test(holder)) {
          continue;
        }

        Optional<Facet> parse =
            (Optional<Facet>) parserPair.getKey().parse(this.holder, this.parent);
        if (parse.isPresent()) {
          parsed.add(new FacetWithParser(parserPair.getKey(), parse.get()));
        } else if (parserPair.getKey().required()) {
          throw new ParsingException(
              "Missing required facet: "
                  + parserPair.getKey().getClass().getSimpleName().replace("Parser", ""));
        }
      }
    }
  }

  /**
   * A simple container class used to keep a facet and parser in a nice package that can be passed
   * around and stored.
   *
   * @param <F> type of facet
   */
  public static class FacetWithParser<F extends Facet> {

    public final FacetParser<F> parser;
    public final F parsed;

    /**
     * Constructor.
     *
     * @param parser used to parse {@link F}
     * @param parsed facet which was parsed from the parser
     */
    FacetWithParser(FacetParser<F> parser, F parsed) {
      this.parser = parser;
      this.parsed = parsed;
    }
  }
}
