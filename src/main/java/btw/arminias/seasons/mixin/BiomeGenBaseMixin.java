package btw.arminias.seasons.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.arminias.seasons.BiomeCustomSeason;
import btw.arminias.seasons.SeasonsAddonMod;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
@Mixin(BiomeGenBase.class)
public abstract class BiomeGenBaseMixin implements BiomeCustomSeason {

    @Shadow public float rainfall;
    @Shadow public float temperature;

    @Shadow public abstract float getFloatTemperature();

    @Shadow public abstract float getFloatRainfall();

    @Unique
    private SplineInterpolator sp_temp;
    @Unique
    private SplineInterpolator sp_temp_plus;
    @Unique
    private SplineInterpolator sp_rain;
    @Unique
    @Environment(EnvType.CLIENT)
    private int[] seasonColorLookupGrass;
    @Unique
    @Environment(EnvType.CLIENT)
    private int[] seasonColorLookupFoliage;

    @Unique
    private final int[] monthColorLookupGrass = new int[SeasonsAddonMod.MONTHS];
    @Unique
    private final int[] monthColorLookupFoliage = new int[SeasonsAddonMod.MONTHS];

    @Inject(method = "<init>", at = @At("RETURN"))
    private void callCustomInit(int par1, CallbackInfo ci) {
        // General
        seasonsAddon$customInit();
        sp_temp_plus = CommonFunctions.createSplineSeason(Arrays.asList(0f, 0.1f, 0.0f, 0.0f));

        if (!MinecraftServer.getIsServer()) {
            // Only in this super class
            int seasons = seasonsAddon$getSeasons();
            seasonsAddon$setSeasonColorLookupGrass(new int[seasons]);
            seasonsAddon$setSeasonColorLookupFoliage(new int[seasons]);
            // Only in this super class
            updateColorCache(false);
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method= "setTemperatureRainfall", at = @At("RETURN"))
    private void callGenerateColorCache(CallbackInfoReturnable<BiomeGenBase> cir) {
        updateColorCache(true);
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private void updateColorCache(boolean override) {
        int seasons = seasonsAddon$getSeasons();
        for (int season = 0; season < seasons; season++) {
            float r = MathHelper.clamp_float(rainfall * seasonsAddon$getRainfallSpline().getUnit((float) season / seasons), 0F, 1F);
            float t = MathHelper.clamp_float(temperature * seasonsAddon$getTemperatureSpline().getUnit((float) season / seasons), 0F, 1F);
            int color = ColorizerGrass.getGrassColor(t, r);
            getSeasonSpecificGrassColorCached(season, color, override);
            color = ColorizerFoliage.getFoliageColor(t, r);
            getSeasonSpecificFoliageColorCached(season, color, override);
        }
        for (int month = 0; month < SeasonsAddonMod.MONTHS; month++) {
            seasonsAddon$getMonthSpecificColorCached(month, true, true);
            seasonsAddon$getMonthSpecificColorCached(month, false, true);
        }
    }

    @Inject(method = "getEnableSnow", at = @At("RETURN"), cancellable = true)
    private void snowIfCold(CallbackInfoReturnable<Boolean> cir) {
        if (getFloatTemperature() < 0.3F) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canRainInBiome", at = @At("RETURN"), cancellable = true)
    private void noRainIfNoRainfall(CallbackInfoReturnable<Boolean> cir) {
        if (getFloatRainfall() <= 0.14F) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getFloatTemperature", at = @At("RETURN"), cancellable = true)
    private void adaptTemperature(CallbackInfoReturnable<Float> cir) {
            cir.setReturnValue((float) ((temperature + modifier_temp_plus()) * modifier(false)));
    }

    @Inject(method = "getIntTemperature", at = @At("RETURN"), cancellable = true)
    private void adaptTemperatureInt(CallbackInfoReturnable<Integer> cir) {
            cir.setReturnValue((int) (((temperature + modifier_temp_plus()) * modifier(false)) * 65536.0F));
    }

    @Inject(method = "getFloatRainfall", at = @At("RETURN"), cancellable = true)
    private void adaptRainfall(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((float) (rainfall * modifier(true)));
    }

    @Inject(method = "getIntRainfall", at = @At("RETURN"), cancellable = true)
    private void adaptRainfallInt(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) ((rainfall * modifier(true)) * 65536.0F));
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getBiomeGrassColor", at = @At("RETURN"), cancellable = true)
    private void adaptGrass(CallbackInfoReturnable<Integer> cir) {
        int month = SeasonsAddonMod.getMonth(SeasonsAddonMod.getSeasonTicks());
        cir.setReturnValue(seasonsAddon$getMonthSpecificColorCached(month, true, false));
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getBiomeFoliageColor", at = @At("RETURN"), cancellable = true)
    private void adaptFoliage(CallbackInfoReturnable<Integer> cir) {
        int month = SeasonsAddonMod.getMonth(SeasonsAddonMod.getSeasonTicks());
        cir.setReturnValue(seasonsAddon$getMonthSpecificColorCached(month, false, false));
    }

    @Environment(EnvType.CLIENT)
    public int seasonsAddon$getMonthSpecificColorCached(int month, boolean grass, boolean override) {
        int[] arr = grass ? monthColorLookupGrass : monthColorLookupFoliage;
        int i = arr[month];
        if (i == 0 || override) {
            int seasons = seasonsAddon$getSeasons();
            int months = SeasonsAddonMod.MONTHS;
            assert months > seasons;
            Float[] seasonArr = new Float[seasons+1];
            Float[] redArr = new Float[seasons+1];
            Float[] greenArr = new Float[seasons+1];
            Float[] blueArr = new Float[seasons+1];

            for (int j = 0; j < seasons+1; j++) {
                seasonArr[j] = (float) j / seasons;
                int c = grass ? seasonColorLookupGrass[j % seasons] : seasonColorLookupFoliage[j % seasons];
                int red   = (c >> 16) & 0xFF;
                int green = (c >> 8)  & 0xFF;
                int blue  = (c >> 0)  & 0xFF;
                redArr[j] = (float) red;
                greenArr[j] = (float) green;
                blueArr[j] = (float) blue;
            }
            SplineInterpolator sp_r = SplineInterpolator.createMonotoneCubicSpline(
                    Arrays.asList(seasonArr), Arrays.asList(redArr), months
                );
            SplineInterpolator sp_g = SplineInterpolator.createMonotoneCubicSpline(
                    Arrays.asList(seasonArr), Arrays.asList(greenArr), months
                );
            SplineInterpolator sp_b = SplineInterpolator.createMonotoneCubicSpline(
                    Arrays.asList(seasonArr), Arrays.asList(blueArr), months
                );
            int red = (int) sp_r.getUnit((float)month/months);
            int green = (int) sp_g.getUnit((float)month/months);
            int blue = (int) sp_b.getUnit((float)month/months);
            arr[month] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
            i = arr[month];
        }
        return i;
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private int getSeasonSpecificGrassColorCached(int season, int color, boolean override) {
        int i = seasonColorLookupGrass[season];
        if (i == 0 || override) {
            i = seasonsAddon$getSeasonSpecificColor(season, color);
            seasonColorLookupGrass[season] = i;
        }
        return i;
    }

    @Unique
    @Environment(EnvType.CLIENT)
    private int getSeasonSpecificFoliageColorCached(int season, int color, boolean override) {
        int i = seasonColorLookupFoliage[season];
        if (i == 0 || override) {
            i = seasonsAddon$getSeasonSpecificColor(season, color);
            seasonColorLookupFoliage[season] = i;
        }
        return i;
    }

    @Unique
    private double modifier(boolean rain) {
        float seasonTicks = SeasonsAddonMod.getSeasonTicks();
        return rain ? seasonsAddon$getRainfallSpline().getUnit(seasonTicks) : seasonsAddon$getTemperatureSpline().getUnit(seasonTicks);
    }

    @Unique
    protected double modifier_temp_plus() {
        float seasonTicks = SeasonsAddonMod.getSeasonTicks();
        return sp_temp_plus.getUnit(seasonTicks);
    }

    @Override
    public SplineInterpolator seasonsAddon$getTemperatureSpline() {
        return sp_temp;
    }

    @Override
    public SplineInterpolator seasonsAddon$getRainfallSpline() {
        return sp_rain;
    }

    @Override
    public int seasonsAddon$getSeasons() {
        return 4;
    };

    @Override
    @Environment(EnvType.CLIENT)
    public void seasonsAddon$setSeasonColorLookupGrass(int[] arr) {
        seasonColorLookupGrass = arr;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void seasonsAddon$setSeasonColorLookupFoliage(int[] arr) {
        seasonColorLookupFoliage = arr;
    }

    @Override
    public void seasonsAddon$customInit() {
        // General
        sp_temp = CommonFunctions.createSplineSeason(Arrays.asList(1.7f, 2.3f, 0.7f, 0.3f));
        sp_rain = CommonFunctions.createSplineSeason(Arrays.asList(1.7f, 0.3f, 2.3f, 1.0f));
    }

    @Unique
    private int getSeason(float x) {
        int seasons = seasonsAddon$getSeasons();
        return (int) ((x % 1) * seasons);
    }
}

