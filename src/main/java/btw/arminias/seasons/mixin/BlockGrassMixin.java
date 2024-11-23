package btw.arminias.seasons.mixin;

import btw.arminias.seasons.CropClimateInfluenced;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin extends Block implements CropClimateInfluenced {
    @Shadow public abstract boolean isSparse(IBlockAccess blockAccess, int x, int y, int z);

    @Shadow public abstract void setFullyGrown(World world, int x, int y, int z);

    @Unique
    private SplineInterpolator sp_temp;
    @Unique
    private SplineInterpolator sp_rain;
    @Unique
    private SplineInterpolator sp_fail;

    protected BlockGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }


    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BlockGrass;canGrassSpreadFromLocation(Lnet/minecraft/src/World;III)Z", ordinal = 0))
    private boolean canGrassSpreadFromLocationMixin(World world, int x, int y, int z) {
        if (!BlockGrass.canGrassSpreadFromLocation(world, x, y, z)) {
            return false;
        }
        float growthMultiplier = seasonsAddon$getGrowthMultiplier(world, x, y, z);
        if (growthMultiplier < 1F) {
            return world.rand.nextFloat() < growthMultiplier;
        }
        while (growthMultiplier > 2F) {
            growthMultiplier -= 1F;
            doGrowthTick(world, x, y, z);
        }
        growthMultiplier -= 1F; // We will return true at the end (one additional growth tick)
        if (world.rand.nextFloat() < growthMultiplier) {
            doGrowthTick(world, x, y, z);
        }
        return true;
    }


    // This is copied from the original updateTick method
    @Unique
    private void doGrowthTick(World world, int x, int y, int z) {
        if (world.rand.nextFloat() <= 0.8f) {
            BlockGrass.checkForGrassSpreadFromLocation(world, x, y, z);
        }
        if (this.isSparse(world, x, y, z) && world.rand.nextInt(12) == 0) {
            this.setFullyGrown(world, x, y, z);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void doCustomInit(int par1, CallbackInfo ci) {
        seasonsAddon$customInit();
    }

    @Override
    public SplineInterpolator seasonsAddon$getFailureSpline() {
        return sp_fail;
    }

    @Override
    public float seasonsAddon$getGrowthMultiplier(World world, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        float rain = biome.getFloatRainfall();
        float temp = biome.getFloatTemperature();
        return 0.15F + seasonsAddon$getTemperatureSpline().get(temp) * seasonsAddon$getRainfallSpline().get(rain);
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
                Arrays.asList(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.8F, 0.9F, 1.2F, 1.4F, 2.0F, 3.0F),
                Arrays.asList(0.25F, 0.4F, 0.65F, 0.7F, 0.92F, 1.0F, 1.1F, 1.4F, 1.2F, 0.9F, 0.6F),
                40
        );
        sp_rain = SplineInterpolator.createMonotoneCubicSpline(
                Arrays.asList(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.8F, 0.9F, 1.1F, 1.4F, 2.0F, 3.0F),
                Arrays.asList(0.25F, 0.35F, 0.55F, 0.75F, 0.92F, 1.0F, 1.1F, 1.2F, 1.4F, 1.2F, 0.5F),
                40
        );
        sp_fail = CommonFunctions.createUnitSpline(
                Arrays.asList(0.9F, 0.825F, 0.75F, 0.6F, 0.4F, 0F, 0F, 0F),
                20, false
        );
    }
}
