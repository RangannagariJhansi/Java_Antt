package wizard.common.communication;

public enum MessageType {
    GAME_ERROR         ("<GAME_ERROR>"),
    GAME_STATUS        ("<GAME_STATUS>"),
    UPDATE_HAND        ("<UPDATE_HAND>"),
    ASK_PREDICTION     ("<ASK_PREDICTION>"),
    ASK_TRICK_START    ("<ASK_TRICK_START>"),
    ASK_TRICK_CARD     ("<ASK_TRICK_CARD>"),

    ANSWER_PREDICTION  ("<ANSWER_PREDICTION>"),
    ANSWER_TRICK_START ("<ANSWER_TRICK_START>"),
    ANSWER_TRICK_CARD  ("<ANSWER_TRICK_CARD>");

    private final String representation;

    MessageType(final String representation) {
        this.representation = representation;
    }

    public String toString() {
        return representation;
    }
}
