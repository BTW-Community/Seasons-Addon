package btw.arminias.seasons;

import btw.arminias.seasons.utils.SplineInterpolator;

public interface ClimateInfluenced {
    SplineInterpolator seasonsAddon$getTemperatureSpline();
    SplineInterpolator seasonsAddon$getRainfallSpline();

    void seasonsAddon$customInit();
}
