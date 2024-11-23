package btw.arminias.seasons.mixin;

import btw.arminias.seasons.CropClimateInfluenced;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.Block;
import net.minecraft.src.BlockCrops;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(BlockCrops.class)
public abstract class BlockCropsMixin extends Block implements CropClimateInfluenced {
    private SplineInterpolator sp_temp;
    private SplineInterpolator sp_rain;
    private SplineInterpolator sp_fail;

    private BlockCropsMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void doCustomInit(int par1, CallbackInfo ci) {
        seasonsAddon$customInit();
    }

    @Inject(method = "getBaseGrowthChance", at = @At(value = "RETURN"), cancellable = true)
    private void adaptGrowthChance(World world, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(seasonsAddon$getGrowthMultiplier(world, i, j, k) * cir.getReturnValue());
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
                Arrays.asList(0.1F,  0.2F, 0.3F, 0.4F, 0.5F,  0.7F, 0.9F,  1.1F, 1.4F,  2.0F, 3.0F),
                Arrays.asList(0.25F, 0.4F, 0.6F, 0.8F, 0.92F, 1.2F, 1.35F, 1.5F, 1.35F, 0.9F, 0.6F),
                40
        );
        sp_rain = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F,  0.2F,  0.3F,  0.4F,  0.5F,  0.7F,  0.9F, 1.1F, 1.4F, 2.0F, 3.0F),
                Arrays.asList(0.25F, 0.35F, 0.55F, 0.75F, 0.92F, 1.35F, 1.7F, 1.8F, 1.9F, 2.4F, 1.2F),
                40
        );
        sp_fail = CommonFunctions.createUnitSpline(
                Arrays.asList(0.9F, 0.825F, 0.75F, 0.6F, 0.4F, 0F, 0F, 0F),
                20, false
        );
    }
}
