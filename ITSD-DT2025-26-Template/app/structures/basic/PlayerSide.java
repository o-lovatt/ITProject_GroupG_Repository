public enum PlayerSide {
    HUMAN_LEFT,
    AI_RIGHT;

    // Return the opposite side e.g. HUMAN_LEFT.opposite() == AI_RIGHT
    public PlayerSide opposite() {
        if (this == HUMAN_LEFT) {
            return AI_RIGHT;
        } else {
            return HUMAN_LEFT;
        }
    }
}
