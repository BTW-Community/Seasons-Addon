package btw.arminias.seasons.mixin;


import btw.block.blocks.CropsBlock;
import btw.block.blocks.DailyGrowthCropsBlock;
import btw.arminias.seasons.CropClimateInfluenced;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.Random;

@Mixin(DailyGrowthCropsBlock.class)
public abstract class DailyGrowthCropsBlockMixin extends CropsBlock implements CropClimateInfluenced {

    @Shadow protected abstract void updateFlagForGrownToday(World world, int i, int j, int k);

    private DailyGrowthCropsBlockMixin(int iBlockID) {
        super(iBlockID);
    }
    @Unique
    private SplineInterpolator sp_temp;
    @Unique
    private SplineInterpolator sp_rain;
    @Unique
    private SplineInterpolator sp_fail;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void doCustomInit(int iBlockID, CallbackInfo ci) {
        seasonsAddon$customInit();
    }

    @Inject(method = "attemptToGrow", at = @At(value = "INVOKE", target = "Lbtw/block/blocks/DailyGrowthCropsBlock;incrementGrowthLevel(Lnet/minecraft/src/World;III)V"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void randomGrowthClimate(World world, int x, int y, int z, Random rand, CallbackInfo ci, int timeOfDay, Block blockBelow, float growthChance) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        float rain = biome.getFloatRainfall();
        float temp = biome.getFloatTemperature();
        float mult = 0.15F + sp_temp.get(temp) * sp_rain.get(rain);
        if (mult < 1F) {
            if (rand.nextFloat() < sp_fail.getUnit(mult)) {
                // Fail
                CommonFunctions.sendMultiplierVisualization(world, x, y, z, "smoke");
                updateFlagForGrownToday(world, x, y, z);
            } else {
                // Success
                incrementGrowthLevel(world, x, y, z);
                updateFlagForGrownToday(world, x, y, z);
            }
        } else {
            incrementGrowthLevel(world, x, y, z);
            boolean sent = false;
            mult -= 1F;
            while (mult > 0F) {
                if (!isFullyGrown(world, x, y, z)) {
                    if (mult > 1F) {
                        if (rand.nextFloat() <= growthChance) {
                            if (!sent) {
                                CommonFunctions.sendMultiplierVisualization(world, x, y, z, "happyVillager");
                                sent = true;
                            }
                            incrementGrowthLevel(world, x, y, z);
                            mult -= 1F;
                        }
                    } else {
                        if (rand.nextFloat() < mult && rand.nextFloat() <= growthChance) {
                            if (!sent) {
                                CommonFunctions.sendMultiplierVisualization(world, x, y, z, "happyVillager");
                                sent = true;
                            }
                            incrementGrowthLevel(world, x, y, z);
                            mult -= 1F;
                        }
                    }
                } else {
                    break;
                }
            }
            updateFlagForGrownToday(world, x, y, z);
        }
        ci.cancel();
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        BiomeGenBase biome = world.getBiomeGenForCoords(i, k);
        float rain = biome.getFloatRainfall();
        float temp = biome.getFloatTemperature();
        float mult = sp_temp.get(temp) * sp_rain.get(rain) + 0.15F;
        System.out.println(rain + ", " + temp + ", " + mult);
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
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
    public void seasonsAddon$customInit() {
        sp_temp = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.7F, 0.9F, 1.1F, 1.4F, 2.0F, 3.0F),
                Arrays.asList(0.25F, 0.4F, 0.6F, 0.8F, 0.92F, 1.2F, 1.35F, 1.5F, 1.35F, 0.9F, 0.6F),
                40
        );
        sp_rain = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.7F, 0.9F, 1.1F, 1.4F, 2.0F, 3.0F),
                Arrays.asList(0.25F, 0.35F, 0.55F, 0.75F, 0.92F, 1.35F, 1.7F, 1.8F, 1.9F, 2.4F, 1.5F),
                40
        );
        sp_fail = CommonFunctions.createUnitSpline(
                Arrays.asList(0.9F, 0.825F, 0.75F, 0.6F, 0.4F, 0F, 0F, 0F),
                20, false);
    }
}





















