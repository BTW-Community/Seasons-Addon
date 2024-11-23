package btw.arminias.seasons.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.BiomeGenSwamp;
import net.minecraft.src.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeGenSwamp.class)
public abstract class BiomeGenSwampMixin extends BiomeGenBase {

    protected BiomeGenSwampMixin(int par1) {
        super(par1);
    }

    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "getBiomeGrassColor", at = @At(value = "STORE"), name = "var1")
    private double fixColorGrass1(double x) {
        return MathHelper.clamp_float((float) x, 0F, 1F);
    }

    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "getBiomeGrassColor", at = @At(value = "STORE"), name = "var3")
    private double fixColorGrass2(double x) {
        return MathHelper.clamp_float((float) x, 0F, 1F);
    }

    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "getBiomeFoliageColor", at = @At(value = "STORE"), name = "var1")
    private double fixColorFoliage1(double x) {
        return MathHelper.clamp_float((float) x, 0F, 1F);
    }

    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "getBiomeFoliageColor", at = @At(value = "STORE"), name = "var3")
    private double fixColorFoliage2(double x) {
        return MathHelper.clamp_float((float) x, 0F, 1F);
    }


    /*@Environment(EnvType.CLIENT)
    @Redirect(method = "getBiomeGrassColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ColorizerGrass;getGrassColor(DD)I"))
    private int adaptGrass(double a, double b) {
        return super.getBiomeGrassColor();
    }

    @Environment(EnvType.CLIENT)
    @Redirect(method = "getBiomeFoliageColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ColorizerFoliage;getFoliageColor(DD)I"))
    private int adaptFoliage(double a, double b) {
        return super.getBiomeFoliageColor();
    }*/

    @Environment(EnvType.CLIENT)
    @Inject(method = "getBiomeGrassColor", at = @At("RETURN"), cancellable = true)
    private void getBiomeGrassColor(CallbackInfoReturnable<Integer> cir) {
        int color = cir.getReturnValue();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int super_color = super.getBiomeGrassColor();
        int super_r = (super_color >> 16) & 0xFF;
        int super_g = (super_color >> 8) & 0xFF;
        int super_b = super_color & 0xFF;

        int new_r = (r + super_r) / 2;
        int new_g = (g + super_g) / 2;
        int new_b = (b + super_b) / 2;

        int new_color = (new_r << 16) | (new_g << 8) | new_b;
        cir.setReturnValue(new_color);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getBiomeFoliageColor", at = @At("RETURN"), cancellable = true)
    private void getBiomeFoliageColor(CallbackInfoReturnable<Integer> cir) {
        int color = cir.getReturnValue();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int super_color = super.getBiomeFoliageColor();
        int super_r = (super_color >> 16) & 0xFF;
        int super_g = (super_color >> 8) & 0xFF;
        int super_b = super_color & 0xFF;

        int new_r = (r + super_r) / 2;
        int new_g = (g + super_g) / 2;
        int new_b = (b + super_b) / 2;

        int new_color = (new_r << 16) | (new_g << 8) | new_b;
        cir.setReturnValue(new_color);
    }

}
