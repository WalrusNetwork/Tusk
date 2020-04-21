package network.walrus.utils.bukkit.world;

// by the way, this region file handler was created based on the divulged region file format:
// http://mojang.com/2011/02/16/minecraft-save-file-format-in-beta-1-3/

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.walrus.utils.bukkit.block.CoordXZ;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * A wrapper class for minecraft world data files.
 *
 * @author Brett Flannigan
 */
public class WorldFileData {
  private transient World world;
  private transient File regionFolder = null;
  private transient File[] regionFiles = null;
  private transient Map<CoordXZ, List<Boolean>> regionChunkExistence =
      Collections.synchronizedMap(new HashMap<CoordXZ, List<Boolean>>());

  private WorldFileData(World world) {
    this.world = world;
  }

  /**
   * Attempt to create a new object for the specified world. If null is returned, there was a data
   * validity problem.
   *
   * @param world to create the data
   * @return wrapper created from the world
   */
  public static WorldFileData create(World world) {
    WorldFileData newData = new WorldFileData(world);

    newData.regionFolder = new File(newData.world.getWorldFolder(), "region");
    if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
      // check for region folder inside a DIM* folder (DIM-1 for nether, DIM1 for end, DIMwhatever
      // for custom world types)
      File[] possibleDimFolders =
          newData.world.getWorldFolder().listFiles(new DimFolderFileFilter());
      for (File possibleDimFolder : possibleDimFolders) {
        File possible =
            new File(
                newData.world.getWorldFolder(),
                possibleDimFolder.getName() + File.separator + "region");
        if (possible.exists() && possible.isDirectory()) {
          newData.regionFolder = possible;
          break;
        }
      }
      if (!newData.regionFolder.exists() || !newData.regionFolder.isDirectory()) {
        newData.sendMessage(
            "Could not validate folder for world's region files. Looked in "
                + newData.world.getWorldFolder().getPath()
                + " for valid DIM* folder with a region folder in it.");
        return null;
      }
    }

    // Accepted region file formats: MCR is from late beta versions through 1.1, MCA is from 1.2+
    newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCA"));
    if (newData.regionFiles == null || newData.regionFiles.length == 0) {
      newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCR"));
      if (newData.regionFiles == null || newData.regionFiles.length == 0) {
        newData.sendMessage(
            "Could not find any region files. Looked in: " + newData.regionFolder.getPath());
        return null;
      }
    }

    return newData;
  }

  /** @return number of region files this world has */
  public int regionFileCount() {
    return regionFiles.length;
  }

  /** @return folder where world's region files are located */
  public File regionFolder() {
    return regionFolder;
  }

  /** @return entire list of region files */
  public File[] regionFiles() {
    return regionFiles.clone();
  }

  /**
   * Get a region file by index
   *
   * @param index to get the region file at
   * @return region file at the specified index
   */
  private File regionFile(int index) {
    if (regionFiles.length < index) return null;
    return regionFiles[index];
  }

  /**
   * Get the X and Z world coordinates of the region from the filename
   *
   * @param index to get the region file at
   * @return X/Z coordinated of the specified region
   */
  public CoordXZ regionFileCoordinates(int index) {
    File regionFile = this.regionFile(index);
    String[] coords = regionFile.getName().split("\\.");
    int x, z;
    try {
      x = Integer.parseInt(coords[1]);
      z = Integer.parseInt(coords[2]);
      return new CoordXZ(x, z);
    } catch (Exception ex) {
      sendMessage("Error! Region file found with abnormal name: " + regionFile.getName());
      return null;
    }
  }

  /**
   * Find out if the chunk at the given coordinates exists.
   *
   * @param x of the chunk
   * @param z of the chunk
   * @return if the chunk exists
   */
  private boolean doesChunkExist(int x, int z) {
    CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
    List<Boolean> regionChunks = this.getRegionData(region);
    return regionChunks.get(coordToRegionOffset(x, z));
  }

  /**
   * Find out if the chunk at the given coordinates has been fully generated. Minecraft only fully
   * generates a chunk when adjacent chunks are also loaded.
   *
   * @param x of the chunk
   * @param z of the chunk
   * @return if the chunk if fully generated
   */
  public boolean isChunkFullyGenerated(
      int x,
      int z) { // if all adjacent chunks exist, it should be a safe enough bet that this one is
    // fully generated
    return !(!doesChunkExist(x, z)
        || !doesChunkExist(x + 1, z)
        || !doesChunkExist(x - 1, z)
        || !doesChunkExist(x, z + 1)
        || !doesChunkExist(x, z - 1));
  }

  /**
   * Update the existence status of a chunk
   *
   * @param x of the chunk
   * @param z of the chunk
   */
  public void chunkExistsNow(int x, int z) {
    CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
    List<Boolean> regionChunks = this.getRegionData(region);
    regionChunks.set(coordToRegionOffset(x, z), true);
  }

  /**
   * Convert x/z coordinates to a region file index
   *
   * <p>region is 32 * 32 chunks chunk pointers are stored in region file at position: x + z*32 (32
   * * 32 chunks = 1024) x and z values can be world-based chunk coordinates or local-to-region
   * chunk coordinates
   *
   * @param x coordinate
   * @param z coordinate
   * @return region file index
   */
  private int coordToRegionOffset(int x, int z) {
    // "%" modulus is used to convert potential world coordinates to definitely be local region
    // coordinates
    x = x % 32;
    z = z % 32;
    // similarly, for local coordinates, we need to wrap negative values around
    if (x < 0) x += 32;
    if (z < 0) z += 32;
    // return offset position for the now definitely local x and z values
    return (x + (z * 32));
  }

  private List<Boolean> getRegionData(CoordXZ region) {
    List<Boolean> data = regionChunkExistence.get(region);
    if (data != null) return data;

    // data for the specified region isn't loaded yet, so init it as empty and try to find the file
    // and load the data
    data = new ArrayList<Boolean>(1024);
    for (int i = 0; i < 1024; i++) {
      data.add(Boolean.FALSE);
    }

    for (int i = 0; i < regionFiles.length; i++) {
      CoordXZ coord = regionFileCoordinates(i);
      // is this region file the one we're looking for?
      if (!coord.equals(region)) continue;

      int counter = 0;
      try {
        RandomAccessFile regionData = new RandomAccessFile(this.regionFile(i), "r");
        // first 4096 bytes of region file consists of 4-byte int pointers to chunk data in the file
        // (32*32 chunks = 1024; 1024 chunks * 4 bytes each = 4096)
        for (int j = 0; j < 1024; j++) {
          // if chunk pointer data is 0, chunk doesn't exist yet; otherwise, it does
          if (regionData.readInt() != 0) data.set(j, true);
          counter++;
        }
        regionData.close();
      } catch (FileNotFoundException ex) {
        sendMessage(
            "Error! Could not open region file to find generated chunks: "
                + this.regionFile(i).getName());
      } catch (IOException ex) {
        sendMessage(
            "Error! Could not read region file to find generated chunks: "
                + this.regionFile(i).getName());
      }
    }
    regionChunkExistence.put(region, data);
    //		testImage(region, data);
    return data;
  }

  // send a message to the server console/log and possibly to an in-game player
  private void sendMessage(String text) {
    Bukkit.getLogger().info("[WorldData] " + text);
  }

  /** File filter used for region files */
  private static class ExtFileFilter implements FileFilter {
    String ext;

    ExtFileFilter(String extension) {
      this.ext = extension.toLowerCase();
    }

    @Override
    public boolean accept(File file) {
      return (file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(ext));
    }
  }

  /** File filter used for DIM* folders (for nether, End, and custom world types) */
  private static class DimFolderFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
      return (file.exists()
          && file.isDirectory()
          && file.getName().toLowerCase().startsWith("dim"));
    }
  }
}
