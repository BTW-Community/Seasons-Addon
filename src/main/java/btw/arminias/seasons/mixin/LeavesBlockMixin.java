package btw.arminias.seasons.mixin;

import btw.arminias.seasons.SeasonsAddonMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockLeaves;
import net.minecraft.src.BlockLeavesBase;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockLeaves.class)
public abstract class LeavesBlockMixin extends BlockLeavesBase {

    public LeavesBlockMixin(int par1, Material par2Material, boolean par3) {
        super(par1, par2Material, par3);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "randomDisplayTick", at = @At("RETURN"))
    private void createLeafParticles(World world, int i, int j, int k, Random rand, CallbackInfo ci) {
        boolean isRaining = world.isRainingAtPos(i, j + 5, k);
        if (world.isThundering() && isRaining) {
            if (rand.nextInt(2) == 0) {
                world.spawnParticle("seasons_leaf", i + rand.nextFloat(), j, k + rand.nextFloat(), -0.225, -0.1 - rand.nextFloat() * 0.05, (rand.nextFloat() - 0.5) * 0.3);
            }
        }
        else if (isRaining) {
            if (rand.nextInt(3) == 0) {
                world.spawnParticle("seasons_leaf", i + rand.nextFloat(), j, k + rand.nextFloat(), -0.15, -0.075 - rand.nextFloat() * 0.05, (rand.nextFloat() - 0.5) * 0.2);
            }
        }
        else if (SeasonsAddonMod.isSeasonalAutumn()) {
            if (rand.nextInt(25) == 0) {
                world.spawnParticle("seasons_leaf", i + rand.nextFloat(), j, k + rand.nextFloat(), -0.04, -0.025 - rand.nextFloat() * 0.01, (rand.nextFloat() - 0.5) * 0.03);
            }
        }
    }
}
