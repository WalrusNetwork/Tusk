package network.walrus.games.core.map.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * A map folder located on disk.
 *
 * @author Avicus Network
 */
public class LocalWorldSource implements WorldSource {

  /** Library that the source is a part of. */
  private final LocalWorldLibrary library;
  /** Directory containing the source files. */
  private final File folder;
  /** The source's {@code map.xml} file. */
  private final File xmlFile;

  /**
   * Constructor.
   *
   * @param library library that the source is a part of
   * @param folder directory containing the source files
   * @param xml the source's {@code map.xml} file
   */
  public LocalWorldSource(LocalWorldLibrary library, File folder, File xml) {
    this.library = library;
    this.folder = folder;
    this.xmlFile = xml;
  }

  @Override
  public String getName() {
    return this.folder.getName();
  }

  @Override
  public InputStream getConfig() throws Exception {
    return new FileInputStream(this.xmlFile);
  }

  @Override
  public InputStream getFile(String path) throws FileNotFoundException {
    return new FileInputStream(new File(this.folder, path));
  }

  @Override
  public File source() {
    return this.folder;
  }

  @Override
  public LocalWorldLibrary getLibrary() {
    return library;
  }

  public File getFolder() {
    return folder;
  }

  public File getXmlFile() {
    return xmlFile;
  }
}
