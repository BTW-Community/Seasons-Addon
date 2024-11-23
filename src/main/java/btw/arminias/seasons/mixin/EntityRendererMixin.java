package btw.arminias.seasons.mixin;

import btw.arminias.seasons.SeasonsAddonMod;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Group(name = "renderRainSnow", min=1, max=1)
    @ModifyVariable(method = "renderRainSnow", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityRenderer;rendererUpdateCount:I", ordinal = 0), name = "rainDist")
    private byte modifyRainDist(byte rainDist) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            return 15;
        }
        return rainDist;
    }

    @Group(name = "renderRainSnow", min=1, max=1)
    @ModifyVariable(method = "renderRainSnow", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityRenderer;rendererUpdateCount:I", ordinal = 0), ordinal = 4)
    private int modifyRainDist2(int rainDist) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            return 15;
        }
        return rainDist;
    }
}
