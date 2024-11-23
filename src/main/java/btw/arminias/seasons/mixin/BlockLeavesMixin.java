package btw.arminias.seasons.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockLeaves;
import net.minecraft.src.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLeaves.class)
public class BlockLeavesMixin {
    @Environment(EnvType.CLIENT)
    @Inject(method = "colorMultiplier", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void modifyBirchColor(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, CallbackInfoReturnable<Integer> cir) {
        int c = cir.getReturnValue();

        int r = (c >> 16) & 0xFF;
        int g = (c >> 8) & 0xFF;
        int b = c & 0xFF;

        int var6 = 0;
        int var7 = 0;
        int var8 = 0;

        for (int var9 = -1; var9 <= 1; ++var9)
        {
            for (int var10 = -1; var10 <= 1; ++var10)
            {
                int var11 = par1IBlockAccess.getBiomeGenForCoords(par2 + var10, par4 + var9).getBiomeFoliageColor();
                var6 += (var11 & 16711680) >> 16;
                var7 += (var11 & 65280) >> 8;
                var8 += var11 & 255;
            }
        }

        cir.setReturnValue(((1 * r + (var6 / 9 & 255)) / 2 << 16) | ((1 * g + (var7 / 9 & 255)) / 2 << 8) | (b - (int) (0.4F * (var8 / 9 & 255))) & 255);
    }
}
