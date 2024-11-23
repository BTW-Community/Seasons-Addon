package btw.arminias.seasons.mixin;

import net.minecraft.src.GuiButtonLanguage;
import net.minecraft.src.GuiConfirmOpenLink;
import net.minecraft.src.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "initGui()V")
	private void init(CallbackInfo info) {
		System.out.println("This line is printed by an example mod mixin!");
	}

	@Redirect(method = "initGui", at = @At(value = "NEW", args = "class=net/minecraft/src/GuiButtonLanguage"))
	private GuiButtonLanguage openLink(int j, int k, int i) {
		System.out.println("This line is printed by an example mod mixin!");
		return new GuiButtonLanguage(j, k, i);
	}
}
