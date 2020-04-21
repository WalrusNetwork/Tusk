package network.walrus.utils.parsing.facet.parse.configurator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * A parser which will always return the supplied facet for each parsing session.
 *
 * @param <F> type of facet being enabled
 * @author Austin Mayes
 */
public class ConstantFacetParser<F extends Facet> implements FacetParser<F> {

  private final Class<? extends F> facetClazz;
  private List<Class> argTypes;
  private List<Object> args;

  /**
   * Constructor.
   *
   * @param facetClazz to create for each parsing routine
   * @param argTypes types of the facet's constructor
   * @param args to pass in for construction
   */
  public ConstantFacetParser(Class<? extends F> facetClazz, Class[] argTypes, Object[] args) {
    this.facetClazz = facetClazz;
    this.argTypes = new ArrayList<>(Arrays.asList(argTypes));
    this.args = new ArrayList<>(Arrays.asList(args));
  }

  @Override
  public Optional<F> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    try {
      List<Class> argTypes = new ArrayList<>(this.argTypes);
      List<Object> args = new ArrayList<>(this.args);
      argTypes.add(0, FacetHolder.class);
      args.add(0, holder);
      F facet =
          facetClazz.getConstructor(argTypes.toArray(new Class[] {})).newInstance(args.toArray());
      return Optional.of(facet);
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new ParsingException("Failed to instantiate constant facet!", e);
    }
  }
}
