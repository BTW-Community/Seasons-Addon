package btw.arminias.seasons.mixin;

import net.minecraft.src.Packet63WorldParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Packet63WorldParticles.class)
public interface Packet63WorldParticlesAccessor {

    @Accessor
    public void setParticleName(String particleName);

    @Accessor
    public void setPosX(float posX);

    @Accessor
    public void setPosY(float posY);

    @Accessor
    public void setPosZ(float posZ);

    @Accessor
    public void setOffsetX(float offsetX);

    @Accessor
    public void setOffsetY(float offsetY);

    @Accessor
    public void setOffsetZ(float offsetZ);

    @Accessor
    public void setSpeed(float speed);

    @Accessor
    public void setQuantity(int quantity);
}
