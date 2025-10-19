package btw.arminias.seasons;

public interface BiomeCustomSeason extends ClimateInfluenced {
    void seasonsAddon$setSeasonColorLookupGrass(int[] arr);
    void seasonsAddon$setSeasonColorLookupFoliage(int[] arr);
    void seasonsAddon$updateColorCache(boolean override);

    default int seasonsAddon$getSeasonSpecificColor(int season, int color) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = (color >> 0) & 0xFF;
        switch (season) {
            case 0:
            case 1:
                break;
            case 2:
                red = Math.min(((int)(red * 1.15) + 45), 255) & 0x0000FF;
                green = (int) (green * 0.65) & 0x0000FF;
                blue = (int) (blue * 0.55) & 0x0000FF;
                break;
            case 3:
                red = (int) (red * 0.85) & 0x0000FF;
                green = (int) (green * 0.95) & 0x0000FF;
                blue = Math.min(((int)(blue * 1.0) + 40), 255) & 0x0000FF;
                break;
        }
        return ((alpha << 24) | (red << 16) | (green << 8) | blue);
    }

    int seasonsAddon$getMonthSpecificColorCached(int month, boolean grass, boolean override);

    int seasonsAddon$getSeasons();
    //void setSeasons(int seasons);

}
