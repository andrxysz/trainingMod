package br.mod.trainingmod.config;

public enum TrainingDifficulty {
    EASY("easy", 2.6D),
    NORMAL("normal", 3.0D),
    HARD("hard", 3.35D),
    EXTREME("extreme", 3.7D);

    private final String key;
    private final double hitReach;

    TrainingDifficulty(String key, double hitReach) {
        this.key = key;
        this.hitReach = hitReach;
    }

    public String getKey() {
        return key;
    }

    public double getHitReach() {
        return hitReach;
    }

    public static TrainingDifficulty fromString(String raw) {
        for (TrainingDifficulty difficulty : values()) {
            if (difficulty.key.equalsIgnoreCase(raw)) {
                return difficulty;
            }
        }
        return null;
    }
}

