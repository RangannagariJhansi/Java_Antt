package wizard.common;

public enum GameStatus {
    WAITING_TRUMP_DECISION_OTHER,
    WAITING_TRUMP_DECISION,
    WAITING_PREDICTION_OTHER,
    WAITING_PREDICTION,
    WAITING_CARD_OTHER,
    WAITING_CARD,
    UNKNOWN;

    /**
     * Creates a new {@code GameStatus} enum.
     */
    GameStatus() {
        //
    }

    /**
     * Returns String representation of this enum.
     *
     * @return String representation of this enum
     */
    @Override
    public String toString() {
        switch (this) {
            case WAITING_TRUMP_DECISION_OTHER:
                return "Waiting for other player to decide which color is trump";
            case WAITING_TRUMP_DECISION:
                return "Waiting for you to decide which color is trump";
            case WAITING_PREDICTION_OTHER:
                return "Waiting for other players to predict their trick count";
            case WAITING_PREDICTION:
                return "Waiting for you to predict your trick count";
            case WAITING_CARD_OTHER:
                return "Waiting for other player play a card";
            case WAITING_CARD:
                return "Waiting for you to play a card";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }
}
