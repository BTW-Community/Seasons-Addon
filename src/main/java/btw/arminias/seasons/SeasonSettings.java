package btw.arminias.seasons;

public record SeasonSettings(int yearLength, String name) {
    public static final SeasonSettings REALISTIC = new SeasonSettings(96, "Realistic - 12 Moon Cycles");
    public static final SeasonSettings LONG = new SeasonSettings(64, "Long - 8 Moon Cycles");
    public static final SeasonSettings MODERATE = new SeasonSettings(48, "Moderate - 6 Moon Cycles");
    public static final SeasonSettings SHORT = new SeasonSettings(32, "Short - 4 Moon Cycles");

    public static final SeasonSettings[] DEFAULTS = {REALISTIC, LONG, MODERATE, SHORT};

    public static SeasonSettings getSeasonSettings(int days) {
        return switch (days) {
            case 96 -> REALISTIC;
            case 64 -> LONG;
            case 48 -> MODERATE;
            case 32 -> SHORT;
            default -> new SeasonSettings(days, "Custom - " + days / 8 + " Moon Cycles");
        };
    }

    public static int getSeasonSettingsPresetIdx(SeasonSettings settings) {
        if (settings.equals(REALISTIC)) {
            return 0;
        } else if (settings.equals(LONG)) {
            return 1;
        } else if (settings.equals(MODERATE)) {
            return 2;
        } else if (settings.equals(SHORT)) {
            return 3;
        }
        return -1;
    }

    public int getTicksPerYear() {
        return yearLength * 24000;
    }
}
