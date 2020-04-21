package network.walrus.utils.core.parse.complex;

import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.NumberAction;
import network.walrus.utils.core.math.PreparedNumberAction;
import network.walrus.utils.core.parse.ComplexParser;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.NumberActionParser;

/**
 * Parser for parsing {@link PreparedNumberAction}s. For non pre-defined actions, a {@link
 * NumberActionParser} should be used instead.
 *
 * @author Austin Mayes
 */
public class PreparedNumberActionParser implements ComplexParser<PreparedNumberAction> {

  private final SimpleParser<Number> numberParser;
  private final SimpleParser<NumberAction> numberActionParser;

  /**
   * Constructor
   *
   * @param numberParser used to parse modifier values
   * @param numberActionParser used to parse base actions
   */
  public PreparedNumberActionParser(
      SimpleParser<Number> numberParser, SimpleParser<NumberAction> numberActionParser) {
    this.numberParser = numberParser;
    this.numberActionParser = numberActionParser;
  }

  @Override
  public PreparedNumberAction parse(Node node) throws ParsingException {
    Number num = numberParser.parseRequired(node.text());
    NumberAction action =
        numberActionParser.parse(node.attribute("action")).orElse(NumberAction.SET);
    return new PreparedNumberAction(num, action);
  }
}
