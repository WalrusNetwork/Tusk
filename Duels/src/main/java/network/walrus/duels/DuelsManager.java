package network.walrus.duels;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.walrus.duels.commands.DuelsCommandModule;
import network.walrus.utils.bukkit.region.BoundedRegion;
import org.apache.commons.io.FileUtils;
import org.bukkit.util.Vector;

class DuelsManager {

  private final Multimap<ArenaType, ArenaProperties> properties = HashMultimap.create();
  private final File worldFile;
  private final Multimap<ArenaType, Duel> emptyDuels = HashMultimap.create();
  private final List<Duel> activeDuels = Lists.newArrayList();
  private final Map<BoundedRegion, Duel> duelAreas = Maps.newHashMap();

  public DuelsManager(File worldFile) {
    this.worldFile = worldFile;
  }

  void enable() throws Exception {
    String worldPath = DuelsPlugin.instance.getConfig().getString("world-path");
    FileUtils.deleteDirectory(worldFile);
    File source = new File(worldPath);
    FileUtils.copyFile(source, worldFile);

    PropertiesParser parser = new PropertiesParser(this.worldFile);
    this.properties.putAll(parser.parse());

    Vector offset = new Vector(100, 0, 80);
    Vector location = offset;
    for (Entry<ArenaType, ArenaProperties> entry : properties.entries()) {
      for (int i = 0; i < 15; i++) {
        Duel duel = entry.getKey().creationFunction().apply(entry.getValue().clone(location));
        this.emptyDuels.put(entry.getKey(), duel);
        this.duelAreas.put(duel.getProperties().getArena(), duel);
        location = location.clone().add(offset);
      }
    }

    registerCommands();
    registerListeners();
  }

  void disable() {
    for (Duel activeDuel : activeDuels) {
      activeDuel.reset();
    }
  }

  private void registerCommands() {
    BasicBukkitCommandGraph graph = new BasicBukkitCommandGraph(new DuelsCommandModule());
    BukkitIntake intake = new BukkitIntake(DuelsPlugin.instance, graph);

    intake.register();
  }

  private void registerListeners() {}

  public Map<BoundedRegion, Duel> getDuelAreas() {
    return duelAreas;
  }
}
