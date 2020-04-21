package network.walrus.utils.parsing.facet.parse.configurator;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Simple holder class used to represent all registered {@link FacetConfigurator}s.
 *
 * @author Austin Mayes
 */
public class ConfiguratorManager {

  private final Set<FacetConfigurator> configurators = Sets.newLinkedHashSet();

  /**
   * Add a configurator which will be used to configure parsers and facets.
   *
   * @param configurator to add
   */
  public void addConfigurator(FacetConfigurator configurator) {
    this.configurators.add(configurator);
  }

  /**
   * Perform an action on all registered configurators.
   *
   * @param consumer to perform
   */
  public void actOnAll(Consumer<FacetConfigurator> consumer) {
    for (FacetConfigurator configurator : this.configurators) {
      consumer.accept(configurator);
    }
  }
}
