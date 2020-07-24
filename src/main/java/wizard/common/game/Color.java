package wizard.common.game;

public enum Color {
    BLUE   ("Blue"),
    GREEN  ("Green"),
    RED    ("Red"),
    YELLOW ("Yellow"),
    CLEAR  ("none");

    private final String representation;

    /**
     * Creates a new {@code Color} enum with given string representation.
     *
     * @param representation String representation of new Color enum
     */
    Color(final String representation) {
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
