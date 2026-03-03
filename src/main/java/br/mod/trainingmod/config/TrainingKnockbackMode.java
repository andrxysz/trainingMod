package br.mod.trainingmod.config;

public enum TrainingKnockbackMode {
    DEFAULT("default", 0.72D, 1.0D),
    HACKING("hacking", 0.08D, 0.2D),
    LONG_NORMAL("long-normal", 1.2D, 1.05D);

    private final String key;
    private final double horizontalMultiplier;
    private final double verticalMultiplier;

    TrainingKnockbackMode(String key, double horizontalMultiplier, double verticalMultiplier) {
        this.key = key;
        this.horizontalMultiplier = horizontalMultiplier;
        this.verticalMultiplier = verticalMultiplier;
    }

    public String getKey() {
        return key;
    }

    public double getHorizontalMultiplier() {
        return horizontalMultiplier;
    }

    public double getVerticalMultiplier() {
        return verticalMultiplier;
    }

    public static TrainingKnockbackMode fromString(String raw) {
        for (TrainingKnockbackMode mode : values()) {
            if (mode.key.equalsIgnoreCase(raw)) {
                return mode;
            }
        }
        return null;
    }
}

