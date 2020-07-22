package wizard.common.game;

public enum Color {
    BLUE   ("Blue"),
    GREEN  ("Green"),
    RED    ("Red"),
    YELLOW ("Yellow"),
    CLEAR  ("none");

    private final String str;

    Color(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }
}
