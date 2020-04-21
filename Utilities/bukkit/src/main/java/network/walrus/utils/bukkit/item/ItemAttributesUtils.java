package network.walrus.utils.bukkit.item;

import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Class used for managing some special item attributes.
 *
 * @author Rafi Baum
 */
public class ItemAttributesUtils {

  private static final ItemTag.Boolean LOCKED = new ItemTag.Boolean("locked", false);
  private static final ItemTag.Boolean UNSHARABLE = new ItemTag.Boolean("unsharable", false);
  private static final ItemTag.Boolean DEATH_DROP = new ItemTag.Boolean("death-drop", true);

  public static boolean isLocked(@Nullable ItemStack item) {
    return item != null && LOCKED.get(item);
  }

  public static void setLock(ItemMeta item, boolean value) {
    LOCKED.set(item, value);
  }

  public static boolean isNotSharable(@Nullable ItemStack item) {
    return item != null && (UNSHARABLE.get(item) || isLocked(item));
  }

  public static void setNotSharable(ItemMeta item, boolean value) {
    UNSHARABLE.set(item, value);
  }

  public static boolean shouldDeathDrop(@Nullable ItemStack item) {
    return item == null || (DEATH_DROP.get(item) && !isNotSharable(item));
  }

  public static void setShouldDeathDrop(ItemMeta item, boolean value) {
    DEATH_DROP.set(item, value);
  }

  public static void clearAttributes(ItemMeta item) {
    LOCKED.clear(item);
    UNSHARABLE.clear(item);
    DEATH_DROP.clear(item);
  }
}
