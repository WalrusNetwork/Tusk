package network.walrus.utils.core.stream;

import static java.util.Collections.emptySet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Utilities for creating {@link Collector}s.
 *
 * @author Overcast Network
 */
public final class CollectorUtils {

  private CollectorUtils() {}

  /**
   * Create a {@link ImmutableList} from the elements of a {@link java.util.stream.Stream}.
   *
   * @param <T> element type
   * @return collector used to create a {@link ImmutableList}
   */
  public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
    return new ListCollector<>(ImmutableList::copyOf);
  }

  /**
   * Create a {@link ImmutableSet} from the elements of a {@link java.util.stream.Stream}.
   *
   * @param <T> element type
   * @return collector used to create a {@link ImmutableSet}
   */
  public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
    return new ListCollector<>(ImmutableSet::copyOf);
  }

  private static class ListCollector<T, R> implements Collector<T, ArrayList<T>, R> {

    private final Function<ArrayList<T>, R> finisher;

    protected ListCollector(Function<ArrayList<T>, R> finisher) {
      this.finisher = finisher;
    }

    @Override
    public Supplier<ArrayList<T>> supplier() {
      return ArrayList::new;
    }

    @Override
    public BiConsumer<ArrayList<T>, T> accumulator() {
      return List::add;
    }

    @Override
    public BinaryOperator<ArrayList<T>> combiner() {
      return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
      };
    }

    @Override
    public Function<ArrayList<T>, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return emptySet();
    }
  }
}
