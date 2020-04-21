package network.walrus.games.core.facets.filters.context;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Variable;

/**
 * Container for multiple {@link Variable}s used for checks
 *
 * <p>Multiple variables of the same type are not allowed.
 *
 * @author Avicus Network
 */
public class FilterContext {

  /** All of the variables inside of this context */
  private final List<Variable> variables = new ArrayList<>();

  public static FilterContext of(Variable... variables) {
    FilterContext context = new FilterContext();
    for (Variable variable : variables) {
      context.add(variable);
    }
    return context;
  }

  /**
   * Remove a variable from the context
   *
   * @param variable to add
   */
  public void remove(Class<? extends Variable> variable) {
    List<Variable> list = new ArrayList<>();
    for (Variable v : this.variables) {
      if (v.getClass() == variable) {
        list.add(v);
      }
    }
    this.variables.removeAll(list);
  }

  /**
   * Add a variable to the context
   *
   * @param variable to add
   */
  public void add(Variable variable) {
    this.variables.add(variable);
  }

  /**
   * Add a variable to the context while removing all others of the same type.
   *
   * @param variable to add
   */
  public void addDestructively(Variable variable) {
    remove(variable.getClass());
    this.variables.add(variable);
  }

  /**
   * Get a variable from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> Optional<T> getFirst(Class<T> variableType) {
    for (Variable test : this.variables) {
      if (test.getClass() == variableType) {
        return Optional.of((T) test);
      }
    }
    return Optional.empty();
  }

  /**
   * Get a variable from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> Optional<T> getLast(Class<T> variableType) {
    for (Variable test : Lists.reverse(this.variables)) {
      if (test.getClass() == variableType) {
        return Optional.of((T) test);
      }
    }
    return Optional.empty();
  }

  /**
   * Get all variables of type from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> List<T> getAll(Class<T> variableType) {
    List<T> result = new ArrayList<T>();
    for (Variable test : this.variables) {
      if (test.getClass() == variableType) {
        result.add((T) test);
      }
    }
    return result;
  }

  /** @return an exact copy of this context with all variables included */
  public FilterContext duplicate() {
    FilterContext clone = new FilterContext();
    for (Variable variable : this.variables) {
      clone.add(variable);
    }
    return clone;
  }
}
