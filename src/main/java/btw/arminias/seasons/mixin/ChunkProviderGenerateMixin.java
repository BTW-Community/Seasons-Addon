package btw.arminias.seasons.mixin;

import btw.arminias.seasons.utils.CommonFunctions;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {

    @Redirect(method = "replaceBlocksForBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BiomeGenBase;getFloatTemperature()F"))
    private float onlyIceInSnowBiomes(BiomeGenBase biome) {
        return CommonFunctions.onlyIceInSnowBiomes(biome, true);
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;canSnowAt(III)Z"))
    private boolean onlySnowInColdBiomes(World world, int x, int y, int z) {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        return CommonFunctions.onlyIceInSnowBiomes(biome, true) < 0.15F;
    }
}
