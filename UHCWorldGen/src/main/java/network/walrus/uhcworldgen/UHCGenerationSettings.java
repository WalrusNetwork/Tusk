package network.walrus.uhcworldgen;

import java.util.Random;
import network.walrus.sportpaper.api.world.CustomizedGenerationSettings;

/**
 * Sets the world gen settings for UHC worlds.
 *
 * @author Rafi Baum
 */
public class UHCGenerationSettings extends CustomizedGenerationSettings {

  public UHCGenerationSettings() {
    setCaveFrequency(4);
  }

  @Override
  public float getCaveSize(Random random) {
    return random.nextFloat() * 2.0F + ((random.nextFloat() / 2.0F) + 0.5F);
  }

  @Override
  public float getLargeCaveSize(Random random) {
    return ((random.nextFloat() / 2.0F) + 0.5F) * random.nextFloat() * 3.0F + 1.0F;
  }
}
