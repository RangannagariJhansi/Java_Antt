package wizard.server;

import java.util.ArrayList;
import java.util.List;

import wizard.common.cards.Card;
import wizard.common.communication.GameStatus;
import wizard.common.game.Color;
import wizard.common.game.Deck;
import wizard.common.game.Hand;
import wizard.common.game.Trick;

public class Round {

    private final Deck deck;
    private final int round;
    private final List<Player> players;
    private final Card trumpCard;
    private final Color trumpColor;

    private int currentPlayer = 0;

    public Round(int round, final List<Player> players) {
        this.round = round;
        this.players = players;

        System.out.println(this);

        deck = new Deck();

        // Give out cards to players
        for (Player player : players) {
            List<Card> hand = new ArrayList<Card>(round);
            for (int i = 0; i < round; i++) {
                hand.add(deck.takeRandom());
            }
            player.giveHand(new Hand(hand));
        }

        // Determine trump color for this round
        trumpCard = deck.takeRandom();
        trumpColor = trumpCard.getColor();
    }

    @Override
    public String toString() {
        return "Round " +round;
    }

    private String status() {
        String status = "";

        if (trumpColor != Color.CLEAR) {
            status += trumpColor + " is trump.\n";
        }

        for (Player p : players) {
            status += p;
            status += "\n";
        }

        return status;
    }

    private Player currentPlayer() {
        return players.get(currentPlayer);
    }

    private Player nextPlayer() {
        currentPlayer = ++currentPlayer % players.size();
        return currentPlayer();
    }

    public void play() {
        //Ask players for their predictions
        int predictionSum = 0;

        // Ask all players for prediction
        for (int i = 0; i < players.size(); i++) {
            currentPlayer().updateGameStatus(GameStatus.WAITING_PREDICTION);
            players.stream()
                .unordered()
                .parallel()
                .filter(p -> p != currentPlayer())
                .forEach(p -> p.updateGameStatus(GameStatus.WAITING_PREDICTION_OTHER));

            // Predictions must not all come true so prediction of last player
            // is restricted.
            // All but the last player may predict any amount of tricks.
            if (i == players.size() - 1) {
                currentPlayer().askPrediction(round, round - predictionSum);
            } else {
                predictionSum += currentPlayer().askPrediction(round);
            }

            nextPlayer();
        }

        System.out.println(status());

        // Play trick
        for (int trickId = 0; trickId < round; trickId++) {
            Trick trick = new Trick();

            System.out.println("Asking players for their cards...");

            // Ask all players for their cards
            for (int i = 0; i < players.size(); i++) {
                // Send updated or empty trick to players
                players.stream()
                    .unordered()
                    .parallel()
                    .forEach(p -> p.updateTrick(trick.asList()));

                currentPlayer().updateGameStatus(GameStatus.WAITING_CARD);
                players.stream()
                    .unordered()
                    .parallel()
                    .filter(p -> p != currentPlayer())
                    .forEach(p -> p.updateGameStatus(GameStatus.WAITING_CARD_OTHER));
                trick.add(currentPlayer().askTrickCard());

                System.out.println(trick);

                // Send updated trick to players
                players.stream()
                    .unordered()
                    .parallel()
                    .forEach(p -> p.updateTrick(trick.asList()));

                nextPlayer();
            }

            // Determine who took the trick
            Card winnerCard = trick.takenBy(trumpColor);
            int winnerId = (currentPlayer + trick.cardId(winnerCard)) % players.size();
            Player winner = players.get(winnerId);

            System.out.printf("Trick gets taken by card %s by %s\n", winnerCard, winner.getName());

            winner.giveTrick(trick);

            // Player who took the trick gets to start the next trick
            currentPlayer = winnerId;
        }

        // Send empty trick to players to indicate trick has been taken
        players.stream()
            .unordered()
            .parallel()
            .forEach(p -> p.updateTrick(new ArrayList<Card>()));




        // End of Round
        for (Player p : players) {
            p.predictionsToScore();
        }

        System.out.println("End of round\n");
    }
}
