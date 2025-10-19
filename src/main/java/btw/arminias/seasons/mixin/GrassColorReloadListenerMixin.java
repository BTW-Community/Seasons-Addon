package btw.arminias.seasons.mixin;

import btw.arminias.seasons.BiomeCustomSeason;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.FoliageColorReloadListener;
import net.minecraft.src.GrassColorReloadListener;
import net.minecraft.src.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GrassColorReloadListener.class, FoliageColorReloadListener.class})
public class GrassColorReloadListenerMixin {

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void onResourceManagerReloadInject(ResourceManager par1, CallbackInfo ci) {
        for (BiomeGenBase biome : BiomeGenBase.biomeList) {
            if (biome != null) {
                ((BiomeCustomSeason) biome).seasonsAddon$updateColorCache(true);
            }
        }
    }
}
