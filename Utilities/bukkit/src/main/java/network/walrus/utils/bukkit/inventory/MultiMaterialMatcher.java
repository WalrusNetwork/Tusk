package network.walrus.utils.bukkit.inventory;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 * A material matcher which will return true if the supplied material matches any of the matcher's
 * {@link SingleMaterialMatcher}s.
 *
 * @author Avicus Network
 */
public class MultiMaterialMatcher implements MaterialMatcher {

  private final List<SingleMaterialMatcher> matchers;

  /**
   * Constructor
   *
   * @param matchers that should each be checked
   */
  public MultiMaterialMatcher(List<SingleMaterialMatcher> matchers) {
    this.matchers = matchers;
  }

  /**
   * Constructor which accepts a list of materials to match
   *
   * @param materials which this matcher should match
   */
  public MultiMaterialMatcher(Material... materials) {
    List<SingleMaterialMatcher> matchers = new ArrayList<>();
    for (Material material : materials) {
      SingleMaterialMatcher singleMaterialMatcher = new SingleMaterialMatcher(material);
      matchers.add(singleMaterialMatcher);
    }
    this.matchers = matchers;
  }

  @Override
  public boolean matches(Material material, byte data) {
    for (SingleMaterialMatcher matcher : this.matchers) {
      if (matcher.matches(material, data)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Search through all matchers contained in thie object and replace any which matched a defined
   * matcher with another value.
   *
   * @param find matcher used to search for materials
   * @param replace matcher used to generate tbe replacement value
   */
  public void replaceMaterial(MultiMaterialMatcher find, SingleMaterialMatcher replace) {
    for (SingleMaterialMatcher matcher : this.matchers) {
      for (SingleMaterialMatcher findMatcher : find.matchers()) {
        if (findMatcher.matches(matcher.material(), matcher.data().orElse((byte) 0))) {
          this.matchers.remove(matcher);
          this.matchers.add(new SingleMaterialMatcher(replace.material(), replace.data()));
        }
      }
    }
  }

  /**
   * Creates a new multi material matcher where only common materials from the two multi material
   * matchers are kept (performs an and operation on the two material matchers).
   *
   * @param materials the multi material matcher to combine with this multi material matcher
   * @return the and multi material matcher of the two multi material matchers
   */
  public MultiMaterialMatcher combineMaterialMatch(MultiMaterialMatcher materials) {
    List<SingleMaterialMatcher> copy = new ArrayList<>(matchers);
    copy.retainAll(materials.matchers);
    return new MultiMaterialMatcher(copy);
  }

  /** @return all matchers contained in this bundle */
  public List<SingleMaterialMatcher> matchers() {
    return matchers;
  }
}
