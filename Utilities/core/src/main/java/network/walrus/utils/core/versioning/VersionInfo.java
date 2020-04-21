package network.walrus.utils.core.versioning;

import network.walrus.utils.core.stage.Stage;

/**
 * Data about the version and protocol version of some object.
 *
 * @author Austin Mayes
 */
public interface VersionInfo {

  /** @return protocol this object's configuration was written for */
  Version getProto();

  /** @return version of this object */
  Version getVersion();

  /** @return stage of this object */
  Stage stage();
}
