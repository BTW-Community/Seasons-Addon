package btw.arminias.seasons.mixin;

import btw.arminias.seasons.SeasonsAddonMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Unique
    private static final int STAGGER_FACTOR = 200;

    @Unique
    private int lastUpdatedMonth = 0;
    @Unique
    private int staggeredUpdateCounter = 0;

    @Shadow private List worldRenderersToUpdate;

    @Shadow private WorldRenderer[] worldRenderers;

    @Shadow private Minecraft mc;

    @Shadow private WorldRenderer[] sortedWorldRenderers;

    @ModifyConstant(method = "sortAndRender", constant = @Constant(intValue = 10, ordinal = 0))
    private int checkMoreChunks(int original) {
        return 50;
    }

    @Inject(method = "sortAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityLivingBase;lastTickPosX:D", ordinal = 1))
    private void sortAndRenderMixin(EntityLivingBase par1EntityLivingBase, int par2, double par3, CallbackInfoReturnable<Integer> cir) {
        if (mc.theWorld != null) {
            if (SeasonsAddonMod.getMonth(SeasonsAddonMod.getSeasonTicks()) != lastUpdatedMonth) {
                lastUpdatedMonth = SeasonsAddonMod.getMonth(SeasonsAddonMod.getSeasonTicks());
                for (WorldRenderer worldRenderer : worldRenderers) {
                    if (worldRenderer != null) {
                        worldRenderer.markDirty();
                    }
                }
            }

            /*if (staggeredUpdateCounter > 0) {
                for (int i = (sortedWorldRenderers.length * (STAGGER_FACTOR - staggeredUpdateCounter)) / STAGGER_FACTOR;
                         i < (sortedWorldRenderers.length * (STAGGER_FACTOR - staggeredUpdateCounter + 1)) / STAGGER_FACTOR;
                         i++)
                {
                    WorldRenderer worldRenderer = sortedWorldRenderers[i];
                    if (worldRenderer != null) {
                        worldRenderer.markDirty();
                    }
                }
                staggeredUpdateCounter--;
            }*/
        }
    }
}
