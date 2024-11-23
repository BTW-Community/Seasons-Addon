package btw.arminias.seasons;

import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;

public interface CropClimateInfluenced extends ClimateInfluenced {
    SplineInterpolator seasonsAddon$getFailureSpline();

    default float seasonsAddon$getGrowthMultiplier(World world, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        float rain = biome.getFloatRainfall();
        float temp = biome.getFloatTemperature();
        float mult = 0.15F + seasonsAddon$getTemperatureSpline().get(temp) * seasonsAddon$getRainfallSpline().get(rain);
        if (world.rand.nextFloat() > 0.2F) {
            if (mult < 0.5F) {
                CommonFunctions.sendMultiplierVisualization(world, x, y, z, "smoke");
            } else if (mult > 1.65F) {
                CommonFunctions.sendMultiplierVisualization(world, x, y, z, "happyVillager");
            }
        }
        return mult;
    }
}
