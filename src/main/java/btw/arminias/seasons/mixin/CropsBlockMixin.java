package btw.arminias.seasons.mixin;

import api.block.blocks.CropsBlock;
import btw.block.blocks.PlantsBlock;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Random;

@Mixin(CropsBlock.class)
public abstract class CropsBlockMixin extends PlantsBlock {

    @Shadow protected abstract void attemptToGrow(World world, int x, int y, int z, Random rand);

    @Shadow protected abstract boolean isFullyGrown(World world, int i, int j, int k);

    protected CropsBlockMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    /*@ModifyConstant(method = "IncrementGrowthLevel", constant = @Constant(intValue = 1, ordinal = 0))
    private int adaptGrowthClimate(int growthAdd, World world, int i, int j, int k) {
        System.out.println(growthAdd);
        return growthAdd;
    }*/

    /*@Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lbtw/block/blocks/CropsBlock;attemptToGrow(Lnet/minecraft/src/World;IIILjava/util/Random;)V"))
    private void randomGrowthClimate(CropsBlock block, World world, int x, int y, int z, Random rand) {
        //System.out.println(x + " " + y + " " + z);
        attemptToGrow(world, x, y, z, rand);
        if (!isFullyGrown(world, x, y, z)) {
            // Additonal?
        }
    }*/

}
