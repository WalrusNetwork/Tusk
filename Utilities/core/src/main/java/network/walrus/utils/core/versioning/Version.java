package network.walrus.utils.core.versioning;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Implementation of the semantic versioning standard.
 *
 * <p>See https://semver.org/
 *
 * @author Avicus Network
 */
public class Version implements Comparable<Version> {

  private final int major;
  private final int minor;
  private final int patch;

  /**
   * Constructor.
   *
   * @param major version number
   * @param minor version number
   * @param patch version number
   */
  public Version(int major, int minor, int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  @Override
  public String toString() {
    return this.major + "." + this.minor + "." + this.patch;
  }

  @Override
  public int compareTo(Version o) {
    if (this.getMajor() > o.getMajor()) {
      return 1;
    }
    if (this.getMajor() < o.getMajor()) {
      return -1;
    }
    if (this.getMinor() > o.getMinor()) {
      return 1;
    }
    if (this.getMinor() < o.getMinor()) {
      return -1;
    }
    if (this.getPatch() > o.getPatch()) {
      return 1;
    }
    if (this.getPatch() < o.getPatch()) {
      return -1;
    }

    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Version)) {
      return false;
    }

    Version version = (Version) o;

    return new EqualsBuilder()
        .append(getMajor(), version.getMajor())
        .append(getMinor(), version.getMinor())
        .append(getPatch(), version.getPatch())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(getMajor())
        .append(getMinor())
        .append(getPatch())
        .toHashCode();
  }

  /**
   * Determine if the supplied {@link Version} is greater than or equal to this object.
   *
   * @param that to check
   * @return if this is greater or equal to that
   */
  public boolean greaterEqual(Version that) {
    if (this.major >= that.major) {
      return true;
    } else if (this.minor >= that.minor) {
      return true;
    } else {
      return this.patch >= that.patch;
    }
  }

  /**
   * Determine if the supplied {@link Version} is greater than this object.
   *
   * @param that to check
   * @return if this is greater than that
   */
  public boolean greater(Version that) {
    if (this.major > that.major) {
      return true;
    } else if (this.major == that.major && this.minor > that.minor) {
      return true;
    } else {
      return this.major == that.major && this.minor == that.minor && this.patch > that.patch;
    }
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }

  public int getPatch() {
    return patch;
  }
}
