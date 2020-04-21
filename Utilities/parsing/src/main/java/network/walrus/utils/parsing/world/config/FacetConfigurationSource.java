package network.walrus.utils.parsing.world.config;

import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.stage.Staged;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.world.Sourced;

/**
 * A source which is used to create {@link FacetHolder}s.
 *
 * <p>This object should be kept as small as possible, since it stays loaded for the entire lifetime
 * of the {@link ConfiguredWorldManager}. At the point where this is used, all that is known is the
 * parent {@link Node} of the configuration document and some general information like version.
 *
 * <p>At this stage, the configuration is syntactically correct, but may not be semantically
 * correct.
 *
 * @author Avicus Network
 */
public interface FacetConfigurationSource extends Sourced, Staged {

  /** @return node containing all configuration data for this source */
  Node parent();

  /**
   * If this source is allowed to be loaded at the current time. If this is false, the source can
   * only be chosen by admins.
   */
  boolean playable();

  /** @return information describing the version and proto of this source. */
  VersionInfo versionInfo();

  default Stage stage() {
    return versionInfo().stage();
  }
}
