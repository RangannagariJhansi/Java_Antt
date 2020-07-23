package wizard.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Trick;

public class Player {
    private final String name;
    private final PlayerConnection connection;

    private List<Card> hand;
    private int tricks;
    private int prediction;
    private int score;

    public Player(final String name, final PlayerConnection connection) {
        this.name = name;
        this.connection = connection;

        hand = new ArrayList<Card>();
        prediction = -1;
    }

    @Override
    public String toString() {
        String handStr = "[ ";
        for (int i = 0; i < hand.size(); i++) {
            handStr += hand.get(i);

            if (i < hand.size() - 1) {
                handStr += ", ";
            }
        }
        handStr += " ]";

        if (prediction == -1 && hand.size() == 0) {
            return String.format("%s\n", name);
        } else if (prediction == -1) {
            return String.format("%s: %s\n", name, handStr);
        } else {
            return String.format("%s: %s"
                    + " [Predicted: %2d]"
                    + " [Taken: %2d]"
                    + " [Score: %3d]\n",
                    name, handStr, prediction, tricks, score);
        }
    }

    public void giveTrick(final Trick trick) {
        tricks++;
    }

    public String getName() {
        return name;
    }

    public void giveHand(final List<Card> hand) {
        this.hand = hand;
        connection.updateHand(hand.toArray(new Card[hand.size()]));
    }

    public boolean hasCard(final Card card) {
        return hand.contains(card);
    }

    public boolean hasColor(final Color color) {
        return hand.parallelStream().anyMatch(x -> x.getColor() == color);
    }

    public int askPrediction(int upperBorder) {
        return askPrediction(upperBorder, -1);
    }

    public int askPrediction(int upperBorder, int notAllowed) {
        int input = -1;

        boolean done = false;
        while (!done) {
            input = connection.askPrediction(upperBorder, notAllowed);

            if (input < 0) {
                connection.sendGameError("You cannot take less than 0 tricks!");
                continue;
            }

            if (input > upperBorder) {
                String error = String.format("You cannot take %d tricks in this round!\n", input);
                connection.sendGameError(error);
                continue;
            }

            if (input == notAllowed) {
                String error = String.format("You must not predict taking %d tricks", notAllowed);
                connection.sendGameError(error);
                continue;
            }

            done = true;
        }

        this.prediction = input;
        return input;
    }

    public Card askTrickStart() throws IOException {
        return askTrickCard(null);
    }

    public Card askTrickCard(final Trick trick) throws IOException {
        Card selectedCard = null;

        boolean done = false;
        while (!done) {
            // If there is no trick start a new one
            if (trick == null) {
                selectedCard = connection.askTrickStart();
            } else {
                selectedCard = connection.askTrickCard(trick);
            }

            if (!hasCard(selectedCard)) {
                connection.updateHand(hand.toArray(new Card[hand.size()]));
                connection.sendGameError(String.format(
                        "You have selected card '%s', which is not on your hand!\n",
                        selectedCard));
                continue;
            }

            //TODO: Farbe zugeben enforcen!!
            //if (hasColor(color))

            done = true;
        }

        return selectedCard;
    }

    public void predictionsToScore() {
        if (tricks == prediction) {
            // Correct predictions score 20 points
            // plus 10 points for every taken trick.

            System.out.printf("%s predicted correctly and earned 20 + %2d points\n", name, 10 * tricks);

            score += 20;
            score += (10 * tricks);
        } else {
            // Every wrong prediction awards -10 points.

            System.out.printf("%s made a false prediction and lost %2d points\n", name, 10 * Math.abs(tricks - prediction));

            score -= 10 * Math.abs(tricks - prediction);
        }

        // Rest counters for next round
        tricks = 0;
        prediction = -1;
    }
}
