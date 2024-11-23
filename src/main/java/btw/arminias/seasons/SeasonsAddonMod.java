package btw.arminias.seasons;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.WorldServer;

public class SeasonsAddonMod implements ModInitializer {
	public static int YEAR_LENGTH_TEMP_VALUE = 24000 * 8 * 12;
	public static int YEAR_LENGTH_VALUE = 24000 * 8 * 12;
	public static int YEAR_LENGTH_VALUE_S = 24000 * 8 * 12;
	public static final int MONTHS = 12;

    @Environment(EnvType.CLIENT)
    public static float getSeasonTicksC() {
        long seasonTicks = 0;
        if (Minecraft.getMinecraft().theWorld != null) {
            seasonTicks = Minecraft.getMinecraft().theWorld.getWorldTime();
        }
        return seasonTicks / (float) YEAR_LENGTH_VALUE;
    }

    public static float getSeasonTicksS() {
        long seasonTicks = 0;
        WorldServer w = MinecraftServer.getServer().worldServers[0];
        if (w != null) {
            seasonTicks = w.getWorldTime();
        }
        return seasonTicks / (float) YEAR_LENGTH_VALUE_S;
    }

    public static float getSeasonTicks() {
        return MinecraftServer.getServer() != null ? getSeasonTicksS() : getSeasonTicksC();
    }

    public static int getMonth(float x) {
        return (int) ((x % 1) * MONTHS);
    }

    public static float getMonthPercentage(float x) {
        return (x % 1) * MONTHS - getMonth(x);
    }

    public static boolean isSeasonalAutumn() {
        int month = getMonth(SeasonsAddonMod.getSeasonTicks());
        return month > MONTHS * 0.375 && month <= MONTHS * 0.625;
    }

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

}
