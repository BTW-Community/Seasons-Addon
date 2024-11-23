package btw.arminias.seasons.mixin;

import btw.arminias.seasons.utils.CommonFunctions;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.BlockIce;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockIce.class)
public abstract class IceBlockMixin {
    @Shadow protected abstract void melt(World world, int i, int j, int k);

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getSavedLightValue(Lnet/minecraft/src/EnumSkyBlock;III)I"), cancellable = true)
    private void meltIfNotCold(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        if (!world.isSnowingAtPos(x, y, z)) {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            float temp = CommonFunctions.onlyIceInSnowBiomes(biome, false);
            if (temp > 0.4F || (temp > 0.15F && rand.nextFloat() < 0.2F)) {
                melt(world, x, y, z);
                ci.cancel();
            }
        }
    }
}
