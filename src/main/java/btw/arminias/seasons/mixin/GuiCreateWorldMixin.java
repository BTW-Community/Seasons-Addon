package btw.arminias.seasons.mixin;

import btw.arminias.seasons.GuiSeasonSettings;
import btw.arminias.seasons.SeasonSettings;
import btw.arminias.seasons.SeasonsAddonMod;
import btw.arminias.seasons.SettingsProvider;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen implements SettingsProvider {
    @Shadow
    private GuiTextField textboxWorldName;
    private GuiButton buttonSeasonSettings;
    private SeasonSettings settings = SeasonSettings.REALISTIC;

    @Override
    public SeasonSettings seasonsAddon$getSettings() {
        return this.settings;
    }

    @Override
    public void seasonsAddon$setSettings(SeasonSettings settings) {
        this.settings = settings;
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGuiInject(CallbackInfo ci) {
        this.buttonSeasonSettings = new GuiButton(73, ((GuiTextFieldAccessor) textboxWorldName).getXPos() + ((GuiTextFieldAccessor) textboxWorldName).getWidth() + 2, ((GuiTextFieldAccessor) textboxWorldName).getYPos(), 98, 20, "Seasons Settings");
        this.buttonList.add(this.buttonSeasonSettings);
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/src/WorldSettings;)V"))
    public void setTempGlobalSeasonLength(GuiButton button, CallbackInfo ci) {
        SeasonsAddonMod.YEAR_LENGTH_TEMP_VALUE = settings.getTicksPerYear();
    }


    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void actionPerformedInject(GuiButton button, CallbackInfo ci) {
        if (button.id == 73) {
            this.mc.displayGuiScreen(new GuiSeasonSettings(this, settings));
        }
    }

}
