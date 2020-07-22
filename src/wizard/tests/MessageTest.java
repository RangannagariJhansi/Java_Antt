package wizard.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import wizard.common.communication.IntMessage;
import wizard.common.communication.Message;
import wizard.common.communication.MessageType;
import wizard.common.communication.StringMessage;
import wizard.common.communication.VoidMessage;

class MessageTest {

    @Test
    void testMessage() {
        Message m = new VoidMessage(MessageType.ASK_TRICK_CARD);
        assertTrue(m.getType() == MessageType.ASK_TRICK_CARD);
    }

    @Test
    void testStringMessage() {
        Message s1 = new StringMessage(MessageType.ANSWER_PREDICTION, "example");
        assertTrue(s1.getContent() instanceof String);
        assertTrue(s1.getContent().equals("example"));
    }

    @Test
    void testIntMessage() {
        Message i1 = new IntMessage(MessageType.ASK_TRICK_CARD, 42);
        assertTrue(i1.getContent() instanceof Integer);
        assertTrue((Integer)(i1.getContent()) == 42);
    }

    @Test
    void testVoidMessage() {
        Message v1 = new VoidMessage(MessageType.UPDATE_HAND);
        assertTrue(v1.getContent() == null);
    }

}
