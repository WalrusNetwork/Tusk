package network.walrus.utils.core.config;

/**
 * The text inside of a {@link Node}.
 *
 * <p>This is created for all nodes, regardless if they actually have text. In implementations of
 * configuration schemas where text is not a normality, implementations should designate some
 * specially named attribute to serve as the text input, and should therefore document this
 * behaviour clearly.
 *
 * @author Austin Mayes
 */
public interface Text extends StringHolder {}
