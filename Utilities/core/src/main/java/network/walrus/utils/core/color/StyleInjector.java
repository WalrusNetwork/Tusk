package network.walrus.utils.core.color;

import com.google.common.base.Joiner;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import network.walrus.utils.core.translation.TextStyle;

/**
 * This is used to reflectively bind objects to variables in a class based on variable name.
 *
 * @author Austin Mayes
 */
public class StyleInjector {

  static final TextStyle $NULL$ = TextStyle.create();
  private static final TextStyle DEFAULT = TextStyle.create();
  private static final Field MODIFIERS_FIELD;
  private static final Joiner JOINER = Joiner.on("-");

  static {
    try {
      MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
      MODIFIERS_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Map variables to the objects which they represent.
   *
   * @param bundle containing the objects to get mappings from
   * @param clazz containing the variables to map
   * @param prefix of the mapping key
   */
  public static void map(StyleBundle bundle, Class<?> clazz, String prefix) {
    try {
      for (Field field : clazz.getFields()) {
        field.setAccessible(true);
        MODIFIERS_FIELD.set(field, field.getModifiers() & ~Modifier.FINAL);
        if (field.get(null) == $NULL$) {
          String path = prefix + "." + JOINER.join(field.getName().toLowerCase().split("_"));
          path = path.substring(1);
          Optional<TextStyle> style = bundle.get(path);
          if (style.isPresent()) field.set(null, style.get());
          else {
            System.out.println("Missing style: " + path);
            field.set(null, DEFAULT.duplicate());
          }
        }
      }
      for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
        map(
            bundle,
            declaredClass,
            prefix + "." + JOINER.join(declaredClass.getSimpleName().toLowerCase().split("_")));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
