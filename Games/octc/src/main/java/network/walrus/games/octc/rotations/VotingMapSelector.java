package network.walrus.games.octc.rotations;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNGame;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMap;
import network.walrus.games.octc.OCNMessages;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.ubiquitous.bukkit.inventory.WalrusInventory;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItemBuilder;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.types.SettingTypes;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN;
import network.walrus.utils.core.color.NetworkColorConstants.Games.Maps.Vote;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.world.Sourced;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Map selector which allows players to vote for the map they'd like to play.
 *
 * @author Rafi Baum
 */
public class VotingMapSelector implements MapSelectorStrategy {

  public static final Setting<Boolean> SHOW_VOTE_SETTING =
      new Setting<>(
          "games.vote-gui",
          SettingTypes.BOOLEAN,
          true,
          OCNMessages.MAP_VOTE_SHOW_SETTING.with(),
          OCNMessages.MAP_VOTE_SHOW_SETTING_DESC.with());

  private static final Random RANDOM = new Random();
  private static final UnlocalizedFormat MAP_ANNOUNCE_FORMAT = new UnlocalizedFormat("{0}: {1}");

  private final MapSelector mapSelector;
  private final List<OCNMap> queue;
  private final List<OCNMap> selectedMaps;
  private final Map<UUID, OCNMap> votesByUuid;
  private final Map<OCNMap, Integer> votes;
  private final Duration voteDuration;
  private final Duration afterVoteDuration;
  private final int votingMapPool;
  private final Duration voteDelay;
  private WalrusInventory votingInventory;

  public VotingMapSelector(MapSelector mapSelector) {
    this.mapSelector = mapSelector;

    this.queue = Lists.newArrayListWithCapacity(mapSelector.worlds().getSources().size());
    reload();

    this.votingMapPool = OCNGameManager.instance.getConfig().getInt("maps.vote.pool-size", 3);
    this.selectedMaps = Lists.newArrayListWithCapacity(votingMapPool);
    this.votesByUuid = Maps.newHashMap();
    this.votes = Maps.newHashMapWithExpectedSize(votingMapPool);
    this.voteDuration =
        Duration.ofSeconds(OCNGameManager.instance.getConfig().getLong("maps.vote.time", 30));
    this.afterVoteDuration =
        Duration.ofSeconds(OCNGameManager.instance.getConfig().getLong("maps.vote.switch-time", 5));
    this.voteDelay =
        Duration.ofSeconds(OCNGameManager.instance.getConfig().getLong("maps.vote.delay", 5));
  }

  @Override
  public void selectMap() {
    GameTask.of(
            "delayed-map-vote",
            () -> {
              List<Sourced> loadedMaps = mapSelector.worlds().getSources();

              if (loadedMaps.size() <= votingMapPool) {
                // Small map pool
                for (Sourced map : loadedMaps) {
                  selectedMaps.add((OCNMap) map);
                }
              } else {
                // Refresh map queue if undersized
                if (queue.size() < votingMapPool) {
                  reload();
                }

                // Add maps
                for (int i = 0; i < votingMapPool; i++) {
                  OCNMap map = queue.remove(0);
                  selectedMaps.add(map);
                  votes.put(map, 0);
                }
              }

              votingInventory =
                  UbiquitousBukkitPlugin.getInstance()
                      .getInventoryManager()
                      .createInventory(
                          selectedMaps.size() / 9 + 1,
                          builder -> {
                            for (OCNMap map : selectedMaps) {
                              ItemStack item = ((OCNGame) map.game()).getIcon();
                              ItemMeta meta = item.getItemMeta();
                              meta.setDisplayName(
                                  Vote.MAP_VOTE_ITEM_NAME.apply(map.name()).toLegacyText());

                              List<String> description = Lists.newArrayListWithCapacity(1);
                              description.add(
                                  Vote.MAP_VOTE_ITEM_GAMEMODE
                                      .apply(map.game().name())
                                      .toLegacyText());

                              meta.setLore(description);
                              item.setItemMeta(meta);

                              builder.addItem(
                                  InventoryItemBuilder.createItem(item)
                                      .onClick(
                                          (inventory, player) -> {
                                            voteFor(player, map);
                                            player.closeInventory();
                                          }));
                            }

                            builder.setName(OCNMessages.MAP_VOTE_UI_TITLE.with());
                          });

              broadcastMapPool();

              for (Player player : Bukkit.getOnlinePlayers()) {
                if (PlayerSettings.get(player, SHOW_VOTE_SETTING)) {
                  votingInventory.open(player);
                }
              }

              UbiquitousBukkitPlugin.getInstance()
                  .getCountdownManager()
                  .start(new VoteCountdown(voteDuration));
            })
        .later((int) (voteDelay.getSeconds() * 20));
  }

  @Command(
      aliases = {"vote"},
      desc = "Vote for a map at the end of a match")
  public void vote(@Sender Player sender, Optional<String> maybeMapName) {
    if (selectedMaps.isEmpty()) {
      sender.sendMessage(OCNMessages.MAP_VOTE_NONE.with(Vote.MAP_VOTE_NONE));
      return;
    }

    if (!maybeMapName.isPresent()) {
      votingInventory.open(sender);
      return;
    }

    String mapName = maybeMapName.get();
    OCNMap map = null;
    mapName = mapName.toLowerCase();
    for (OCNMap mapInPool : selectedMaps) {
      if (mapInPool.name().toLowerCase().startsWith(mapName)) {
        map = mapInPool;
        break;
      }
    }

    // Try parsing parameter as index number
    if (map == null) {
      int maybeInt = -1;
      try {
        maybeInt = Integer.parseInt(maybeMapName.get());
      } catch (NumberFormatException ignored) {
      }

      if (maybeInt > 0 && maybeInt <= selectedMaps.size()) {
        map = selectedMaps.get(maybeInt - 1);
      } else {
        sender.sendMessage(OCNMessages.MAP_VOTE_NO_MAP.with(Vote.MAP_VOTE_NO_MAP));
        return;
      }
    }

    voteFor(sender, map);
    OCN.Maps.Vote.VOTED.play(sender);
  }

  private void voteFor(Player sender, OCNMap map) {
    OCNMap oldMap = votesByUuid.put(sender.getUniqueId(), map);
    if (oldMap != null) {
      if (oldMap == map) {
        sender.sendMessage(OCNMessages.MAP_VOTE_UNCHANGED.with(Vote.MAP_VOTE_UNCHANGED));
        return;
      }

      sender.sendMessage(
          OCNMessages.MAP_VOTE_CHANGED.with(
              Vote.MAP_VOTE_CHANGED,
              new UnlocalizedText(oldMap.name(), Vote.MAP_VOTE_ITEM_NAME),
              new UnlocalizedText(map.name(), Vote.MAP_VOTE_ITEM_NAME)));
      int oldVotes = votes.get(oldMap);
      votes.put(oldMap, oldVotes - 1);
    } else {
      sender.sendMessage(
          OCNMessages.MAP_VOTE_SUCCESS.with(
              Vote.MAP_VOTE_SUCCESS, new UnlocalizedText(map.name(), Vote.MAP_VOTE_ITEM_NAME)));
    }

    int oldVotes = votes.get(map);
    votes.put(map, oldVotes + 1);
  }

  private void broadcastMapPool() {
    Bukkit.broadcast(OCNMessages.MAP_VOTE_ANNOUNCEMENT.with(Vote.MAP_VOTE_ANNOUNCEMENT));
    for (int i = 0; i < selectedMaps.size(); i++) {
      Bukkit.broadcast(
          MAP_ANNOUNCE_FORMAT.with(
              Vote.MAP_VOTE_ITEM_TEXT.click(makeClickable(selectedMaps.get(i))),
              new LocalizedNumber(i + 1, Vote.MAP_VOTE_ITEM_NUMBER),
              new UnlocalizedText(selectedMaps.get(i).name(), Vote.MAP_VOTE_ITEM_NAME)));
    }
  }

  private void broadcastVotes() {
    for (Entry<OCNMap, Integer> mapEntry : votes.entrySet()) {
      Bukkit.broadcast(
          MAP_ANNOUNCE_FORMAT.with(
              Vote.MAP_VOTE_ITEM_TEXT.click(makeClickable(mapEntry.getKey())),
              new UnlocalizedText(mapEntry.getKey().name(), Vote.MAP_VOTE_ITEM_NAME),
              new LocalizedNumber(mapEntry.getValue(), Vote.MAP_VOTES)));
    }
  }

  private ClickEvent makeClickable(OCNMap map) {
    return new ClickEvent(Action.RUN_COMMAND, "/vote \"" + map.name() + "\"");
  }

  private void cleanupVote() {
    selectedMaps.clear();
    votesByUuid.clear();
    votes.clear();
    votingInventory = null;
  }

  @Override
  public void reload() {
    this.queue.clear();
    for (Sourced source : mapSelector.worlds().getSources()) {
      this.queue.add((OCNMap) source);
    }
    Collections.shuffle(this.queue, RANDOM);
  }

  class VoteCountdown extends Countdown {

    protected VoteCountdown(Duration duration) {
      super(duration);
    }

    @Override
    protected void onTick(Duration elapsedTime, Duration remainingTime) {
      updateBossBar(
          OCNMessages.MAP_VOTE_COUNTDOWN.with(
              Vote.MAP_VOTE_COUNTDOWN_TEXT,
              new UnlocalizedText(
                  StringUtils.secondsToClock(remainingTime.getSeconds()),
                  Vote.MAP_VOTE_COUNTDOWN_CLOCK)),
          elapsedTime);

      if (elapsedTime.getSeconds() == 0 || remainingTime.getSeconds() == 0) {
        return;
      }

      if (remainingTime.getSeconds() % 10 == 0 || remainingTime.getSeconds() == 5) {
        Bukkit.broadcast(OCNMessages.MAP_VOTE_TALLY.with(Vote.MAP_VOTE_TALLY));
        broadcastVotes();
      }

      if (remainingTime.getSeconds() % 10 == 0) {
        OCN.Maps.Vote.VOTE_ENDING.play(Bukkit.getOnlinePlayers());
      }
    }

    @Override
    protected void onEnd() {
      int maxVotes = -1;
      OCNMap map = null;
      for (Entry<OCNMap, Integer> entry : votes.entrySet()) {
        if (entry.getValue() > maxVotes) {
          maxVotes = entry.getValue();
          map = entry.getKey();
        }
      }

      Bukkit.broadcast(
          OCNMessages.MAP_VOTE_TALLY_FINAL.with(
              new UnlocalizedText(map.name(), Vote.MAP_VOTE_ITEM_NAME)));
      OCN.Maps.Vote.VOTE_ENDED.play(Bukkit.getOnlinePlayers());
      broadcastVotes();
      cleanupVote();
      clearBossBars();

      UbiquitousBukkitPlugin.getInstance()
          .getCountdownManager()
          .start(new MapCountdown(afterVoteDuration, map));
    }

    @Override
    protected void onCancel() {
      cleanupVote();
      clearBossBars();
    }

    @Override
    protected String name() {
      return "map-vote-countdown";
    }
  }
}
