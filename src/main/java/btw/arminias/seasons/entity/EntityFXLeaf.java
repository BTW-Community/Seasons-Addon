package btw.arminias.seasons.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;

@Environment(EnvType.CLIENT)
public class EntityFXLeaf extends EntityFX {

    private short bobTimer;
    private float velX;
    private float velY;
    private float velZ;

    public EntityFXLeaf(World par1World, double par2, double par4, double par6, double velX, double velY, double velZ) {
        super(par1World, par2, par4, par6, 0, 0, 0);
        this.setSize(0.01f, 0.01f);
        this.motionX = velX;
        this.motionZ = velZ;
        this.velX = (float) velX;
        this.velY = (float) velY;
        this.velZ = (float) velZ;
        this.bobTimer = 10;
        this.particleMaxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        this.particleGravity = 0.06f;
        int color = Block.leaves.colorMultiplier(worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        this.particleRed = (color >> 16) & 255;
        this.particleGreen = (color >> 8) & 255;
        this.particleBlue = (color >> 0) & 255;
        this.multipleParticleScaleBy(0.25F);
        color = (int) (this.particleRed + this.particleGreen + this.particleBlue) / 3;
        // Increase saturation
        this.particleRed = (int) (this.particleRed + (this.particleRed - color) * (0.9F + this.rand.nextFloat() * 0.2F));
        this.particleGreen = (int) (this.particleGreen + (this.particleGreen - color) * (0.9F + this.rand.nextFloat() * 0.2F));
        this.particleBlue = (int) (this.particleBlue + (this.particleBlue - color) * (0.9F + this.rand.nextFloat() * 0.2F));

        // Decrease brightness
        this.particleRed = (int) (this.particleRed * (0.5F + this.rand.nextFloat() * 0.1F));
        this.particleGreen = (int) (this.particleGreen * (0.5F + this.rand.nextFloat() * 0.1F));
        this.particleBlue = (int) (this.particleBlue * (0.5F + this.rand.nextFloat() * 0.1F));

        this.particleRed = Float.min(255, Float.max(0, this.particleRed));
        this.particleGreen = Float.min(255, Float.max(0, this.particleGreen));
        this.particleBlue = Float.min(255, Float.max(0, this.particleBlue));

    }
    public void onUpdate()
    {
        particleAge++;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY = velY - 0.00125F * particleAge;
        this.motionX = velX;
        this.motionZ = velZ;

        if (this.bobTimer > 0)
        {
            this.bobTimer--;
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
        }

        if (this.onGround)
        {
            this.bobTimer--;
            // bob timer is negative
            float speedMult = 1F + 0.012F * bobTimer;
            speedMult = MathHelper.clamp_float(speedMult, 0.1F, 1.0F);
            this.motionX *= 0.6 * speedMult;
            this.motionZ *= 0.6 * speedMult;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.particleMaxAge-- <= 0)
        {
            this.setDead();
        }

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
        Material var1 = block == null ? Material.air : block.blockMaterial;

        if (var1.isLiquid() || (var1.isSolid() && /* no it's safe because Material air is not solid */ !block.getBlocksMovement(worldObj, x, y, z)))
        {
            double var2 = (float)(y + 1) - BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(x, y, z));

            if (this.posY < var2)
            {
                this.setDead();
            }
        }
    }


    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
        float var8 = (float)5.5f / 16.0f;
        float var9 = var8 + 0.0624375f;
        float var10 = (float)3.5 / 16.0f;
        float var11 = var10 + 0.0624375f;
        float var12 = 0.1f * this.particleScale;
        float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        float var16 = (this.getBrightnessForRender(par2) >> 20) / 16.0F;
        par1Tessellator.setColorRGBA((int) (this.particleRed * var16), (int) (this.particleGreen * var16), (int) (this.particleBlue * var16), 255);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 - par6 * var12, var14 - par4 * var12, var15 - par5 * var12 - par7 * var12, var9, var11);
        par1Tessellator.addVertexWithUV(var13 - par3 * var12 + par6 * var12, var14 + par4 * var12, var15 - par5 * var12 + par7 * var12, var9, var10);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 + par6 * var12, var14 + par4 * var12, var15 + par5 * var12 + par7 * var12, var8, var10);
        par1Tessellator.addVertexWithUV(var13 + par3 * var12 - par6 * var12, var14 - par4 * var12, var15 + par5 * var12 - par7 * var12, var8, var11);
    }
}
