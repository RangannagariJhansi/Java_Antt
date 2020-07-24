package wizard.common.game;

public enum Color {
    BLUE   ("Blue"),
    GREEN  ("Green"),
    RED    ("Red"),
    YELLOW ("Yellow"),
    CLEAR  ("none");

    private final String representation;

    Color(final String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
