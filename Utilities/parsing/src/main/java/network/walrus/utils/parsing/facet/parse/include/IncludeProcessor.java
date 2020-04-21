package network.walrus.utils.parsing.facet.parse.include;

import network.walrus.utils.parsing.world.config.ConfigurationParseException;

/**
 * Object which combines configuration structures from multiple files into one singular document.
 *
 * @author Austin Mayes
 */
public interface IncludeProcessor {

  /** @return if there are still inclusion definitions to handle */
  boolean shouldProcess();

  /**
   * Handle a singular inclusion definition.
   *
   * @throws ConfigurationParseException if the definition has an error
   */
  void process() throws ConfigurationParseException;
}
