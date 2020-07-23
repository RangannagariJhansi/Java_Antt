package wizard.common.game;

public enum Color {
    BLUE   ("Blue"),
    GREEN  ("Green"),
    RED    ("Red"),
    YELLOW ("Yellow"),
    CLEAR  ("none");

    private final String str;

    Color(final String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
