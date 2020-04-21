package network.walrus.utils.bukkit.item;

import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Useful wrappers for NBT tags which can be added to items.
 *
 * @author Overcast Network
 */
public class ItemTag {

  public abstract static class Base<T> {

    protected final java.lang.String name;
    final T defaultValue;

    Base(java.lang.String name, T defaultValue) {
      this.name = name;
      this.defaultValue = defaultValue;
    }

    protected abstract boolean hasPrimitive(NBTTagCompound tag);

    protected abstract T getPrimitive(NBTTagCompound tag);

    protected abstract void setPrimitive(NBTTagCompound tag, T value);

    void clearPrimitive(NBTTagCompound tag) {
      tag.remove(name);
    }

    /**
     * Check if the {@link NBTTagCompound} contains this tag.
     *
     * @param tag to check
     * @return if the compound contains this tag
     */
    public boolean has(@Nullable NBTTagCompound tag) {
      return tag != null && hasPrimitive(tag);
    }

    /**
     * Check if the {@link ItemMeta} contains this tag.
     *
     * @param meta to check
     * @return if the meta contains this tag
     */
    public boolean has(@Nullable ItemMeta meta) {
      return has(NBTUtils.getCustomTag(meta));
    }

    /**
     * Check if the {@link ItemStack} contains this tag.
     *
     * @param stack to check
     * @return if the stack contains this tag
     */
    public boolean has(@Nullable ItemStack stack) {
      return has(NBTUtils.getCustomTag(stack));
    }

    /**
     * Get the value of this tag given a specific {@link NBTTagCompound}. If the tag is null or
     * doesn't contain the tag, the {@link #defaultValue} will be returned.
     *
     * @param tag to get data from
     * @return the value of this tag
     */
    public T get(@Nullable NBTTagCompound tag) {
      if (tag != null && hasPrimitive(tag)) {
        return getPrimitive(tag);
      } else {
        return defaultValue;
      }
    }

    /**
     * Get the value of this tag given specific {@link ItemMeta}. If the meta is null or doesn't
     * contain the tag, the {@link #defaultValue} will be returned.
     *
     * @param meta to get data from
     * @return the value of this tag
     */
    public T get(@Nullable ItemMeta meta) {
      return get(NBTUtils.getCustomTag(meta));
    }

    /**
     * Get the value of this tag given a specific {@link ItemStack}. If the stack is null or doesn't
     * contain the tag, the {@link #defaultValue} will be returned.
     *
     * @param stack to get data from
     * @return the value of this tag
     */
    public T get(@Nullable ItemStack stack) {
      return get(NBTUtils.getCustomTag(stack));
    }

    /**
     * Set a specific value for this tag for an {@link NBTTagCompound}
     *
     * @param tag to apply this tag to
     * @param value to set this tag to for the compound
     */
    public void set(NBTTagCompound tag, T value) {
      if (Objects.equals(value, defaultValue)) {
        clear(tag);
      } else {
        setPrimitive(tag, Preconditions.checkNotNull(value));
      }
    }

    /**
     * Set a specific value for this tag for {@link ItemMeta}
     *
     * @param meta to apply this tag to
     * @param value to set this tag to for the meta
     */
    public void set(ItemMeta meta, T value) {
      set(NBTUtils.getOrCreateCustomTag(meta), value);
    }

    /**
     * Set a specific value for this tag for an {@link ItemStack}
     *
     * @param stack to apply this tag to
     * @param value to set this tag to for the item
     */
    public void set(ItemStack stack, T value) {
      ItemUtils.updateMeta(stack, meta -> set(meta, value));
    }

    /**
     * Clear this tag data from an {@link NBTTagCompound}
     *
     * @param tag to clear data from
     */
    public void clear(@Nullable NBTTagCompound tag) {
      if (tag != null) {
        clearPrimitive(tag);
      }
    }

    /**
     * Clear this tag data from {@link ItemMeta}
     *
     * @param meta to clear data from
     */
    public void clear(@Nullable ItemMeta meta) {
      clear(NBTUtils.getCustomTag(meta));
      NBTUtils.prune(meta);
    }

    /**
     * Clear this tag data from an {@link ItemStack}
     *
     * @param stack to clear data from
     */
    public void clear(@Nullable ItemStack stack) {
      ItemUtils.updateMetaIfPresent(stack, this::clear);
    }
  }

  public static class Boolean extends Base<java.lang.Boolean> {

    /**
     * @param name of the tag
     * @param defaultValue of the tag
     */
    public Boolean(java.lang.String name, java.lang.Boolean defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 1);
    }

    @Override
    protected java.lang.Boolean getPrimitive(NBTTagCompound tag) {
      return tag.getBoolean(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Boolean value) {
      tag.setBoolean(name, value);
    }
  }

  public static class Integer extends Base<java.lang.Integer> {

    /**
     * @param name of the tag
     * @param defaultValue of the tag
     */
    public Integer(java.lang.String name, java.lang.Integer defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 3);
    }

    @Override
    protected java.lang.Integer getPrimitive(NBTTagCompound tag) {
      return tag.getInt(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Integer value) {
      tag.setInt(name, value);
    }
  }

  public static class Double extends Base<java.lang.Double> {

    /**
     * @param name of the tag
     * @param defaultValue of the tag
     */
    public Double(java.lang.String name, java.lang.Double defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 99);
    }

    @Override
    protected java.lang.Double getPrimitive(NBTTagCompound tag) {
      return tag.getDouble(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Double value) {
      tag.setDouble(name, value);
    }
  }

  public static class Float extends Base<java.lang.Float> {

    /**
     * @param name of the tag
     * @param defaultValue of the tag
     */
    public Float(java.lang.String name, java.lang.Float defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 5);
    }

    @Override
    protected java.lang.Float getPrimitive(NBTTagCompound tag) {
      return tag.getFloat(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Float value) {
      tag.setFloat(name, value);
    }
  }

  public static class String extends Base<java.lang.String> {

    /**
     * @param name of the tag
     * @param defaultValue of the tag
     */
    public String(java.lang.String name, java.lang.String defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 8);
    }

    @Override
    protected java.lang.String getPrimitive(NBTTagCompound tag) {
      return tag.getString(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.String value) {
      tag.setString(name, value);
    }
  }
}
