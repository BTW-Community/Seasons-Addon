package btw.arminias.seasons.mixin;

import btw.arminias.seasons.SeasonsAddon;
import btw.arminias.seasons.SeasonsAddonMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow public WorldServer[] worldServers;

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
    private void initialWorldChunkLoadMixin(CallbackInfo ci) {
        int yearLength = this.worldServers[0].getData(SeasonsAddon.YEAR_LENGTH);
        if (yearLength == -1) {
            this.worldServers[0].setData(SeasonsAddon.YEAR_LENGTH, SeasonsAddonMod.YEAR_LENGTH_TEMP_VALUE);
        }
        SeasonsAddonMod.YEAR_LENGTH_VALUE_S = this.worldServers[0].getData(SeasonsAddon.YEAR_LENGTH);
        SeasonsAddonMod.YEAR_LENGTH_VALUE = this.worldServers[0].getData(SeasonsAddon.YEAR_LENGTH);
    }
}
