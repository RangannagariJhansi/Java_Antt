package wizard.server;

import java.util.ArrayList;
import java.util.List;

import wizard.common.GameStatus;
import wizard.common.cards.Card;
import wizard.common.game.Color;
import wizard.common.game.Deck;
import wizard.common.game.Hand;
import wizard.common.game.ScoreBoard;
import wizard.common.game.Trick;

/**
 * {@code Game} object representing a wizard game.
 * Contains all current game status information
 */
public class Game {

    private final List<Player> players;
    private final ScoreBoard scoreBoard;

    private int currentPlayer = 0;
    private Deck deck;
    private Color trumpColor;
    private Card trumpCard;

    /**
     * Create new {@code Game} with given players.
     *
     * @param players List of players playing this {@code Game}
     */
    public Game(List<Player> players) {
        this.players = players;
        this.scoreBoard = new ScoreBoard();
        players.stream().map(Player::getName).forEach(scoreBoard::add);
    }

    /**
     * Returns a string representing the current game status.
     *
     * @return The string representing the current game status
     */
    private String status() {
        String status = "";

        if (trumpColor != Color.CLEAR) {
            status += trumpColor + " is trump.\n";
        }

        for (Player p : players) {
            status += p;
            status += "\n";
        }

        status += scoreBoard.toAsciiString();

        return status;
    }

    /**
     * Returns the players whose turn it is.
     *
     * @return The player whose turn it is
     */
    private Player currentPlayer() {
        return players.get(currentPlayer);
    }

    /**
     * Ends the current players turn and advances to the next player.
     *
     * @return The new player whose turn it is after this function call
     */
    private Player nextPlayer() {
        currentPlayer = ++currentPlayer % players.size();
        return currentPlayer();
    }

    /**
     * Starts playing this game.
     */
    public void play() {
        // Play all rounds
        // Number of rounds is dependent on number of players
        for (int i = 1; i < 60 / players.size(); i++) {
            playRound(i);
        }
    }

    /**
     * Plays one round of this game.
     * The given round index specifies which round is meant to be played
     * and determines the number of cards and number of tricks that get
     * played in this round.
     *
     * @param round The round index of the round to be played
     */
    private void playRound(int round) {
        deck = new Deck();

        // Give out random cards
        giveOutCards(round);

        // Determine trump color for this round
        determineTrump();

        //Ask players for their predictions
        askPredictions(round);

        System.out.println(status());

        // Play all tricks in this round
        for (int i = 0; i < round; i++) {
            playTrick();
        }

        // End of Round
        scoreBoard.predictionsToScore();
        players.stream()
            .unordered()
            .parallel()
            .forEach(p -> p.updateScores(scoreBoard));

        System.out.println("End of round\n");
    }

    /**
     * Give out cards to all players.
     * The given index specifies how many cards each player is supposed
     * to get. It should correspond to the current round index.
     *
     * @param cards How many cards each player is supposed to get
     */
    private void giveOutCards(int cards) {
        // Give out cards to players
        for (Player player : players) {
            List<Card> hand = new ArrayList<Card>(cards);
            for (int i = 0; i < cards; i++) {
                hand.add(deck.takeRandom());
            }
            player.giveHand(new Hand(hand));
        }
    }

    /**
     * Determine the trump color for this round.
     * Trump color mainly gets determined by taking a card from the deck:
     * - If card has a color it is the trump color
     * - If card is a jester there is no trump color
     * - if card is a wizard player can freely choose a trump color
     */
    private void determineTrump() {
        // Determine trump color for this round
        trumpCard = deck.takeRandom();
        trumpColor = trumpCard.getColor();

        // TODO: If card is a jester there is no trump color
        // TODO: If card is a wizard player has to choose a color

        // Send trump color to all players
        for (Player p : players) {
            p.updateTrump(trumpCard, trumpColor);
        }
    }

    /**
     * Ask all players for their trick predictions this round.
     *
     * @param round The round index of the current round
     */
    private void askPredictions(int round) {
        int predictionSum = 0;

        // Send unset predictions to all players
        players.stream()
            .unordered()
            .parallel()
            .forEach(p -> p.updateScores(scoreBoard));

        // Ask all players for prediction
        for (int i = 0; i < players.size(); i++) {
            // Update game status accordingly
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
                int prediction = currentPlayer().askPrediction(round, round - predictionSum);
                scoreBoard.setPredictions(currentPlayer().getName(), prediction);
            } else {
                int prediction = currentPlayer().askPrediction(round);
                scoreBoard.setPredictions(currentPlayer().getName(), prediction);
                predictionSum += prediction;
            }

            // Send updated predictions to all players
            players.stream()
                .unordered()
                .parallel()
                .forEach(p -> p.updateScores(scoreBoard));

            nextPlayer();
        }
    }

    /**
     * Play a trick.
     * - Ask one player after the other for their cards for a trick
     * - Determine which player took the trick
     * - Add / subtract to all players total score
     */
    private void playTrick() {
        // Send empty trick to players
        Trick trick = new Trick();
        players.stream()
            .unordered()
            .parallel()
            .forEach(p -> p.updateTrick(trick.asList()));

        System.out.println("Asking players for their cards...");

        // Ask all players for their cards
        for (int i = 0; i < players.size(); i++) {
            // Update game status for all players
            currentPlayer().updateGameStatus(GameStatus.WAITING_CARD);
            players.stream()
                .unordered()
                .parallel()
                .filter(p -> p != currentPlayer())
                .forEach(p -> p.updateGameStatus(GameStatus.WAITING_CARD_OTHER));

            // Ask player for card and add to the trick
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

        // Player who took the trick gets to start the next trick
        currentPlayer = winnerId;

        scoreBoard.addTrick(winner.getName());

        // Update player state
        players.stream()
            .unordered()
            .parallel()
            .forEach(p -> {
                // Send updated scores to indicate who has taken the trick
                p.updateScores(scoreBoard);

                // Send empty trick to players to indicate trick has been taken
                p.updateTrick(new Trick().asList());
            });
    }
}
