package btw.arminias.seasons.mixin;

import btw.arminias.seasons.BiomeCustomSeason;
import btw.arminias.seasons.utils.CommonFunctions;
import btw.arminias.seasons.utils.SplineInterpolator;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.BiomeGenJungle;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Arrays;

@Mixin(BiomeGenJungle.class)
public abstract class BiomeGenJungleMixin extends BiomeGenBase implements BiomeCustomSeason {

    private SplineInterpolator sp_temp;
    private SplineInterpolator sp_rain;

    protected BiomeGenJungleMixin(int par1) {
        super(par1);
    }

    @Override
    public void seasonsAddon$customInit() {
        sp_rain = CommonFunctions.createSplineSeason(Arrays.asList(3.0F, 0.33F));
        sp_temp = CommonFunctions.createSplineSeason(Arrays.asList(1.25F, 0.75F));
    }

    @Override
    public int seasonsAddon$getSeasonSpecificColor(int season, int color) {
        return color;
    }

    @Override
    public SplineInterpolator seasonsAddon$getTemperatureSpline() {
        return sp_temp;
    }

    @Override
    public SplineInterpolator seasonsAddon$getRainfallSpline() {
        return sp_rain;
    }

    @Override
    public int seasonsAddon$getSeasons() {
        return 2;
    }
}
