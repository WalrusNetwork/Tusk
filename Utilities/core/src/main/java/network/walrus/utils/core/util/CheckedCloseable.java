package network.walrus.utils.core.util;

/**
 * AutoCloseable that doesn't throw checked exceptions from {@link #close}
 *
 * @author Overcast Network
 */
public interface CheckedCloseable extends AutoCloseable {

  @Override
  void close();
}
