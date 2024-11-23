package btw.arminias.seasons.mixin;

import btw.block.blocks.SnowCoverBlock;
import btw.arminias.seasons.utils.CommonFunctions;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(SnowCoverBlock.class)
public class SnowCoverBlockMixin {

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getSavedLightValue(Lnet/minecraft/src/EnumSkyBlock;III)I"), cancellable = true)
    private void meltIfNotCold(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        if (!world.isSnowingAtPos(x, y, z)) {
            int meta = world.getBlockMetadata(x, y, z);
            //System.out.println(x + " " + y + " " + z);
            //if (true) return world.setBlockToAir(x, y, z);
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            float temp = CommonFunctions.onlyIceInSnowBiomes(biome, true);
            if (temp > 0.4F) {
                meta = meta <= 4 ? 0 : meta - 4;
                world.setBlockMetadata(x, y, z, meta);
                ci.cancel();
                //return world.setBlockToAir(x, y, z);
            } else if (temp > 0.15F) {
                meta = meta <= 2 ? 0 : meta - 2;
                world.setBlockMetadata(x, y, z, meta);
                ci.cancel();
            } else if (temp < 0.15F) {
                return;
            }
            if (meta == 0) {
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Inject(method = "canPlaceBlockAt", at = @At(
                value = "INVOKE", target = "Lnet/minecraft/src/Block;getIsBlockWarm(Lnet/minecraft/src/IBlockAccess;III)Z"
            ), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void fixIceStackOverflow(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir, int iBlockBelowID, Block blockBelow) {
        if (blockBelow != null && blockBelow.blockID == Block.ice.blockID && world.getBlockId(i, j, k) == Block.snow.blockID) {
            world.setBlock(i, j, k, 0, 0, 0x1010);
            cir.setReturnValue(true);
        }
    }

}
