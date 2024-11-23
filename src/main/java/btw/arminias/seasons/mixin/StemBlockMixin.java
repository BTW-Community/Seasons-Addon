package btw.arminias.seasons.mixin;

import btw.arminias.seasons.CropClimateInfluenced;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.Block;
import net.minecraft.src.BlockStem;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Random;

@Mixin(BlockStem.class)
public abstract class StemBlockMixin implements CropClimateInfluenced {
    @Unique
    private SplineInterpolator sp_temp;
    @Unique
    private SplineInterpolator sp_rain;
    @Unique
    private SplineInterpolator sp_fail;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void doCustomInit(int iBlockID, Block fruitBlock, CallbackInfo ci) {
        seasonsAddon$customInit();
    }

    @ModifyConstant(method = "checkForGrowth", constant = @Constant(floatValue = 0.2F))
    private float adaptGrowthMultiplier(float original, World world, int i, int j, int k, Random rand) {
        return seasonsAddon$getGrowthMultiplier(world, i, j, k) * original;
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
    public SplineInterpolator seasonsAddon$getFailureSpline() {
        return sp_fail;
    }

    @Override
    public void seasonsAddon$customInit() {
        sp_temp = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F,  0.2F,  0.3F, 0.4F,  0.5F,  0.7F, 0.9F,  1.1F,  1.4F,  2.0F,  3.0F),
                Arrays.asList(0.35F, 0.55F, 0.7F, 0.85F, 0.92F, 1.1F, 1.25F, 1.35F, 1.25F, 0.92F, 0.75F),
                40
        );
        sp_rain = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F,  0.2F,  0.3F, 0.4F,  0.5F,  0.7F, 0.9F,  1.1F, 1.4F,  2.0F, 3.0F),
                Arrays.asList(0.35F, 0.55F, 0.7F, 0.85F, 0.92F, 1.2F, 1.35F, 1.5F, 1.65F, 2.0F, 1.35F),
                40
        );
        sp_fail = CommonFunctions.createUnitSpline(
                Arrays.asList(0.9F, 0.825F, 0.75F, 0.6F, 0.4F, 0F, 0F, 0F),
                20, false);
    }
}
