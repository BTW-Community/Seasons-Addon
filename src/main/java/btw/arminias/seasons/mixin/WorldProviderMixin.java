package btw.arminias.seasons.mixin;

import btw.arminias.seasons.ClimateInfluenced;
import btw.arminias.seasons.SeasonsAddonMod;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(WorldProvider.class)
public abstract class WorldProviderMixin implements ClimateInfluenced {

    // 50% longer = 1.34
    // 33% longer = 1.235
    // 33% shorter = 0.75
    // 50% shorter = 0.585

    // If days are longer, the sun will rise earlier and set later
    // 0.25 is sunset, 0.75 is sunrise
    // 0.0 = 1.0 is noon, 0.5 is midnight

    @Shadow public World worldObj;
    @Unique
    private final SplineInterpolator dayLengthInterpolator = CommonFunctions.createUnitSpline(Arrays.asList(1.0F, 1.34F, 1.0F, 0.585F), SeasonsAddonMod.MONTHS * 4, true);

    @Inject(method = "calculateCelestialAngle", at = @At("RETURN"), cancellable = true)
    private void modifyDayLengthBasedOnSeason(long par1, float par3, CallbackInfoReturnable<Float> cir) {
        float seasonTicks = (float) this.worldObj.getWorldTime() / SeasonsAddonMod.YEAR_LENGTH_VALUE;
        float dayLength = dayLengthInterpolator.getUnit(seasonTicks);
        float angle = cir.getReturnValue();
        float rescaledAngle = angle * 2 - 1F;
        float modifiedAngle = (float) (Math.pow(Math.abs(rescaledAngle), 1.0 / dayLength) * Math.signum(rescaledAngle));
        cir.setReturnValue((modifiedAngle + 1F) / 2F);
    }

    @Inject(method = "getMoonPhase", at = @At("RETURN"), cancellable = true)
    private void fixMoonPhase(long par1, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int)((par1 - 12000L) / 24000L) % 8);
    }
}
