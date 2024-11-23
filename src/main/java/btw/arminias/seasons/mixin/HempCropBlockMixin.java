package btw.arminias.seasons.mixin;

import btw.block.blocks.HempCropBlock;
import btw.arminias.seasons.CropClimateInfluenced;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HempCropBlock.class)
public abstract class HempCropBlockMixin implements CropClimateInfluenced {
    @Inject(method = "getBaseGrowthChance", at = @At(value = "RETURN"), cancellable = true)
    private void adaptGrowthChance(World world, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(seasonsAddon$getGrowthMultiplier(world, i, j, k) * cir.getReturnValue());
    }
}
