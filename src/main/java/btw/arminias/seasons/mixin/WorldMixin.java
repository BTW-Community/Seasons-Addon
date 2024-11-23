package btw.arminias.seasons.mixin;

import btw.arminias.seasons.SeasonsAddonMod;
import btw.arminias.seasons.utils.CommonFunctions;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public WorldInfo worldInfo;

    @Shadow public Random rand;

    @Redirect(method = "canBlockFreeze", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BiomeGenBase;getFloatTemperature()F"))
    private float onlyIceInSnowBiomes(BiomeGenBase biome, int par1, int par2, int par3, boolean natural) {
        return natural ? biome.getFloatTemperature() : CommonFunctions.onlyIceInSnowBiomes(biome, true);
    }

    @Inject(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setRainTime(I)V", shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
    private void consistentlyMoreOftenRain(CallbackInfo ci) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            this.worldInfo.setRainTime(this.rand.nextInt(48000) + 14000);
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setThunderTime(I)V", shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
    private void shiftTowardsLongerThundering(CallbackInfo ci) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            this.worldInfo.setThunderTime(this.rand.nextInt(7800) + 7800);
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setThunderTime(I)V", shift = At.Shift.AFTER, ordinal = 0), cancellable = true)
    private void moreOftenThundering1(CallbackInfo ci) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            this.worldInfo.setThunderTime(this.rand.nextInt(28000) + 1200);
        }
    }

    @Inject(method = "updateWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setThunderTime(I)V", shift = At.Shift.AFTER, ordinal = 2), cancellable = true)
    private void moreOftenThundering2(CallbackInfo ci) {
        if (SeasonsAddonMod.isSeasonalAutumn()) {
            this.worldInfo.setThunderTime(this.rand.nextInt(28000) + 1200);
        }
    }
}
