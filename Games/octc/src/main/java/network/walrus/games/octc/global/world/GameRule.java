package network.walrus.games.octc.global.world;

/**
 * Wrapper for all possible Minecraft game rule values which is used to control parsing.
 *
 * <p>NOTE: Don't change the capitalization, this is how it is in Minecraft.
 *
 * @author Avicus Network
 */
public enum GameRule {
  doFireTick,
  doTileDrops,
  doMobLoot,
  mobGriefing,
  naturalRegeneration,
  doDaylightCycle
}
