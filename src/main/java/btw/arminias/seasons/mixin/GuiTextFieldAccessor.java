package btw.arminias.seasons.mixin;

import net.minecraft.src.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiTextField.class)
public interface GuiTextFieldAccessor {
    @Accessor
    int getXPos();

    @Accessor
    int getYPos();

    @Accessor
    int getWidth();
}
