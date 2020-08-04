package wizard.common.messages;

public enum MessageType {
    GAME_ERROR         ("<GAME_ERROR>"),
    GAME_STATUS        ("<GAME_STATUS>"),
    UPDATE_HAND        ("<UPDATE_HAND>"),
    UPDATE_TRUMP_CARD  ("<UPDATE_TRUMP_CARD>"),
    UPDATE_TRUMP_COLOR ("<UPDATE_TRUMP_COLOR>"),
    UPDATE_TRICK       ("<UPDATE_TRICK>"),
    ASK_PREDICTION     ("<ASK_PREDICTION>"),
    ASK_TRICK_CARD     ("<ASK_TRICK_CARD>"),

    ANSWER_PREDICTION  ("<ANSWER_PREDICTION>"),
    ANSWER_TRICK_CARD  ("<ANSWER_TRICK_CARD>");

    private final String representation;

    /**
     * Creates a new {@code MessageType} enum with given string representation.
     *
     * @param representation String representation of new {@code MessageType} enum
     */
    MessageType(final String representation) {
        this.representation = representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return representation;
    }
}
