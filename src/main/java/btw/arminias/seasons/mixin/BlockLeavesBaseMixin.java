package btw.arminias.seasons.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockLeavesBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFX;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLeavesBase.class)
public class BlockLeavesBaseMixin {
    @Environment(EnvType.CLIENT)
    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void onEntityCollidedWithBlockMixin(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityFX) {
            ci.cancel();
        }
    }
}
