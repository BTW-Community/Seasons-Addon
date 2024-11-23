package btw.arminias.seasons;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;


public class GuiSeasonSettings extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton buttonDone;
    private GuiButton buttonPreset;

    private SeasonSettings settings;
    private int chosenPreset;

    public GuiSeasonSettings(GuiScreen guiScreen, SeasonSettings settings) {
        this.parentScreen = guiScreen;
        this.settings = settings;
        this.chosenPreset = SeasonSettings.getSeasonSettingsPresetIdx(settings);
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));

        // Switcher button
        this.buttonList.add(this.buttonPreset = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 20, "Switch"));

    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Year Length", this.width / 2, 40, 0xffffff);

        this.buttonPreset.displayString = "Preset: " + this.settings.name();

        super.drawScreen(i, j, f);
    }

    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            if (this.settings != null && this.parentScreen instanceof SettingsProvider) {
                ((SettingsProvider) this.parentScreen).seasonsAddon$setSettings(this.settings);
            }
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (guibutton.id == 1) {
            this.chosenPreset = (this.chosenPreset + 1) % SeasonSettings.DEFAULTS.length;
            this.settings = SeasonSettings.DEFAULTS[this.chosenPreset];
        }
    }
}
