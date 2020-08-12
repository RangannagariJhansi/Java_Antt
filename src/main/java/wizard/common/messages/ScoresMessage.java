package wizard.common.messages;

import wizard.common.game.ScoreBoard;

public class ScoresMessage extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new {@code ScoreMessage} with given type and content.
     *
     * @param type The type of this new message
     * @param content The content of this new message
     */
    public ScoresMessage(final MessageType type, final ScoreBoard content) {
        super(type, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreBoard getContent() {
        return (ScoreBoard)super.getContent();
    }
}
