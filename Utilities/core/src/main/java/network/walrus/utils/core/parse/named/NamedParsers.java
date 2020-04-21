package network.walrus.utils.core.parse.named;

import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;

/**
 * Utils for working with {@link NamedParser}s.
 *
 * @author Avicus Network
 */
public final class NamedParsers {

  /**
   * Search for all methods in the supplied {@link Class} with the {@link NamedParser} annotation.
   * Each {@link NamedParser#value()} will be entered into a map as the key with the value being the
   * base method being annotated. Duplicate keys are explicitly not allowed.
   *
   * @param clazz to retrieve methods from
   * @return map of identifier -> method
   */
  public static Map<String, Method> methods(final Class<?> clazz) {
    final Map<String, Method> result = new HashMap<>();
    for (final Method method : clazz.getDeclaredMethods()) {
      @Nullable final NamedParser parser = method.getAnnotation(NamedParser.class);
      if (parser != null) {
        method.setAccessible(true);
        for (String name : parser.value()) {
          Method old;
          if ((old = result.put(name, method)) != null) {
            throw new IllegalStateException(
                String.format(
                    "Attempted to replace %s parser %s with %s",
                    clazz.getName(), old.getName(), method.getName()));
          }
        }
      }
    }
    return result;
  }

  /**
   * Invoke a parse method using the node's name for map lookup.
   *
   * <p>See {@link #invokeMethod(Object, Method, Node, Object[])}.
   *
   * @throws ParsingException if a method matching the key could not be found, or if invocation
   *     fails
   */
  public static <T> T invokeMethod(
      Table<Object, String, Method> parsers, Node node, String notFoundMessage, Object[] methodArgs)
      throws ParsingException {
    for (Table.Cell<Object, String, Method> cell : parsers.cellSet()) {
      if (!cell.getColumnKey().equalsIgnoreCase(node.name())) {
        continue;
      }

      return invokeMethod(cell.getRowKey(), cell.getValue(), node, methodArgs);
    }

    throw new ParsingException(node, notFoundMessage);
  }

  /**
   * Invoke a parse method using the method's return type for map lookup.
   *
   * @param type expected method return type See {@link #invokeMethod(Object, Method, Node,
   *     Object[])}.
   * @throws ParsingException if a method matching the value could not be found, or if invocation
   *     fails
   */
  public static <T> T invokeMethod(
      Table<Object, String, Method> parsers,
      Class<T> type,
      Node node,
      String notFoundMessage,
      Object[] methodArgs)
      throws ParsingException {
    for (Table.Cell<Object, String, Method> cell : parsers.cellSet()) {
      if (type.isAssignableFrom(cell.getValue().getReturnType())) {
        if (cell.getValue().getReturnType().equals(type)
            || cell.getColumnKey().equalsIgnoreCase(node.name())) {
          return invokeMethod(cell.getRowKey(), cell.getValue(), node, methodArgs);
        }
      }
    }

    throw new ParsingException(node, notFoundMessage);
  }

  /**
   * Invoke a specific method.
   *
   * @param container instance containing the method
   * @param method to be invoked
   * @param node to pass exceptions to if parsing fails
   * @param methodArgs method arguments
   * @param <T> method return type
   * @return what the invoked method returned
   * @throws ParsingException if parsing fails either due to reflection issues or because the
   *     invoked method threw an exception
   */
  private static <T> T invokeMethod(Object container, Method method, Node node, Object[] methodArgs)
      throws ParsingException {
    try {
      return (T) method.invoke(container, methodArgs);
    } catch (Exception e) {
      e.printStackTrace();
      if (e.getCause() != null) {
        if (e.getCause() instanceof ParsingException) {
          throw (ParsingException) e.getCause();
        }
        throw new ParsingException(node, e.getCause());
      }
      throw new ParsingException(node, e);
    }
  }
}
