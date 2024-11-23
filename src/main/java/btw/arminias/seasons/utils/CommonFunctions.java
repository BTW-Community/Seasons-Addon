package btw.arminias.seasons.utils;

import btw.arminias.seasons.SeasonsAddonMod;
import btw.arminias.seasons.mixin.Packet63WorldParticlesAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Packet63WorldParticles;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.List;

public class CommonFunctions {

    /**
     *  Calculates temperature in a way where snow biomes mostly stay snowy and icy.
     *  @param onlyVeryCold Only freeze very cold normal biomes - instead of just moderately cold ones.
     *                     All in addition to snow biomes, which are always frozen.
    **/
    public static float onlyIceInSnowBiomes(BiomeGenBase biome, boolean onlyVeryCold) {
        float current_temp = biome.getFloatTemperature();
        if (current_temp > 0.3F) {
            return current_temp;
        }
        // Always freeze snow biomes
        else if (biome.temperature < 0.15F) {
            return biome.temperature;
        }
        else if (onlyVeryCold) {
            if (current_temp < 0.065F) {
                return current_temp;
            }
            else {
                return 0.2F;
            }
        }
        else {
            return current_temp;
        }
    }

    public static SplineInterpolator createSplineSeason(List<Float> values) {
        return createUnitSpline(values, SeasonsAddonMod.MONTHS, true);
    }

    public static SplineInterpolator createUnitSpline(List<Float> values, int cacheSize, boolean addFirst) {
        values = new ArrayList<>(values);
        int steps = values.size();
        if (addFirst) {
            values.add(values.get(0));
        }
        List<Float> list = new ArrayList<>();
        long limit = values.size();
        for (float f = 0.0F; ; f = f + 1.0F / steps) {
            if (limit-- == 0) break;
            list.add(f);
        }
        return SplineInterpolator.createMonotoneCubicSpline(
                list,
                values,
                cacheSize);
    }

    public static void sendMultiplierVisualization(World world, int x, int y, int z, String name) {
        Packet63WorldParticles packet = new Packet63WorldParticles();
        ((Packet63WorldParticlesAccessor) packet).setParticleName(name);
        ((Packet63WorldParticlesAccessor) packet).setPosX(x+0.5F);
        ((Packet63WorldParticlesAccessor) packet).setPosY(y+0.1F);
        ((Packet63WorldParticlesAccessor) packet).setPosZ(z+0.5F);
        ((Packet63WorldParticlesAccessor) packet).setOffsetX(0.15F);
        ((Packet63WorldParticlesAccessor) packet).setOffsetY(0.05F);
        ((Packet63WorldParticlesAccessor) packet).setOffsetZ(0.15F);
        ((Packet63WorldParticlesAccessor) packet).setSpeed(0.02F);
        ((Packet63WorldParticlesAccessor) packet).setQuantity(20);
        MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer())
                .sendToAllNear(x, y, z, 400, world.worldInfo.getVanillaDimension(), packet);
    }
}
