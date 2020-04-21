package network.walrus.utils.parsing.facet.holder;

import java.util.List;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.DocumentParser.FacetWithParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Handler which is used by the {@link DocumentParser} to handle pre and post parsing calls which
 * are used for validations and other minimal cleanup of errors/legacy data.
 *
 * <p>Any method in the chain can throw a {@link ParsingException} if there is a configuration
 * error. If an error is thrown in any part of the parsing process, the chain will halt there and
 * the following methods will not be called.
 *
 * @param <H> base holder type that this parser is responsible for providing configuration data for
 * @author Austin Mayes
 */
public interface FacetHolderParser<H extends FacetHolder> {

  /**
   * The first method which is called in the parsing chain. This is called right after the holder
   * has been constructed from the base data. At this point, the holder should be expected to
   * contain no runtime data. If an error is thrown from this method, the main parse method will not
   * be called.
   *
   * @param holder which the facets will be registered inside of. At this stage, the holder will
   *     only contain base data created during initial creation.
   * @param parent node containing all configuration data for this holder
   * @throws ParsingException if there is a configuration error
   */
  void preParse(H holder, Node parent) throws ParsingException;

  /**
   * The last method which is called in the parsing chain. This is called after all facets have been
   * parsed and added to the holder context, and any internal validations regarding cross-references
   * have been ran and have passed. If an error is thrown during the main parsing method, this will
   * not be called.
   *
   * @param holder which the facets will be registered inside of. At this stage, the holder will
   *     contain all parsed facets, and the data within them, and any other data which was gathered
   *     during pre and the main parsing procedures.
   * @param parsed mapping of {@link FacetParser}s to the {@link Facet}s the succeded in parsing.
   *     Only parsers which returned a facet during the main parsing procedure will be available at
   *     the stage.
   * @throws ParsingException if there is a configuration error
   */
  void postParse(H holder, List<FacetWithParser> parsed) throws ParsingException;
}
