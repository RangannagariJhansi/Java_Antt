package wizard.common.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import wizard.server.Player;

public class ScoreBoard {

    private class Triple {
        public int prediction = -1;
        public int tricks = 0;
        public int score = 0;
    }

    private Map<String, Triple> map;

    /**
     * Creates a new {@code ScoreBoard}.
     */
    public ScoreBoard() {
        this.map = new HashMap<String, Triple>();
    }

    /**
     * Returns a String representation of this object.
     * String will have multiple lines and end with a newline character.
     *
     * @return The String representation of this object
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (Entry<String, Triple> entry : map.entrySet()) {
            String player = entry.getKey();
            int prediction = entry.getValue().prediction;
            int tricks = entry.getValue().tricks;
            int score = entry.getValue().score;

            String line = String.format("%s:"
                + " [Predicted: %2d]"
                + " [Taken: %2d]"
                + " [Score: %3d]",
                player, prediction, tricks, score);

            str.append(line);
            str.append('\n');
        }

        return str.toString();
    }

    /**
     * Returns a String representation of this object as an ASCII table.
     * String will have multiple lines and end with a newline character.
     *
     * @return The String representation of this object
     */
    public String toAsciiString() {
        // Column headings for table
        final String playerHeading = " Player ";
        final String predictionHeading = " Prediction ";
        final String trickHeading = " Tricks ";
        final String scoreHeading = " Scores ";

        // Function to pad a string to a given length
        final BiFunction<String, Integer, String> stringPad = (x, j) -> {
            // If x is already padded just return it
            if (x.length() >= j) {
                return x;
            }

            String str = x;
            for (int i = 0; i < j - x.length(); i++) {
                str += ' ';
            }

            return str;
        };

        final List<String> players = new ArrayList<String>(map.size());
        final List<Integer> predictions = new ArrayList<Integer>(map.size());
        final List<String> tricks = new ArrayList<String>(map.size());
        final List<String> scores = new ArrayList<String>(map.size());

        // Convert all values to lists of equal size
        for (Entry<String, Triple> entry : map.entrySet()) {
            players.add(" " +entry.getKey() +" ");
            predictions.add(entry.getValue().prediction);
            tricks.add(Integer.toString(entry.getValue().tricks));
            scores.add(Integer.toString(entry.getValue().score));
        }


        // Determine width of columns
        final int nameWidth = Stream.concat(
                players.stream(),
                Stream.of(playerHeading))
            .unordered()
            .parallel()
            .map(String::length)
            .max((a, b) -> a - b)
            .orElse(0);
        final int predictionWidth = predictionHeading.length();
        final int trickWidth = trickHeading.length();
        final int scoreWidth = scoreHeading.length();
        final int totalWidth = 1 + nameWidth + 1 + predictionWidth + 1
            + trickWidth + 1 + scoreWidth + 1;

        // Start building the table
        StringBuilder ascii = new StringBuilder();

        // Upper border
        for (int i = 0; i < totalWidth; i++) {
            ascii.append('-');
        }
        ascii.append('\n');

        // Headings
        ascii.append('|');
        ascii.append(stringPad.apply(playerHeading, nameWidth));
        ascii.append('|');
        ascii.append(stringPad.apply(predictionHeading, predictionWidth));
        ascii.append('|');
        ascii.append(stringPad.apply(trickHeading, trickWidth));
        ascii.append('|');
        ascii.append(stringPad.apply(scoreHeading, scoreWidth));
        ascii.append('|');
        ascii.append('\n');

        // Separator line
        for (int i = 0; i < totalWidth; i++) {
            ascii.append('-');
        }
        ascii.append('\n');

        // Data lines
        for (int i = 0; i < players.size(); i++) {
            ascii.append('|');
            ascii.append(stringPad.apply(players.get(i), nameWidth));

            ascii.append('|');
            {
                String prd;
                if (predictions.get(i) == -1) {
                    prd = "?";
                } else {
                    prd = Integer.toString(predictions.get(i));
                }
                ascii.append(stringPad.apply(prd, predictionWidth));
            }

            ascii.append('|');
            ascii.append(stringPad.apply(tricks.get(i), trickWidth));

            ascii.append('|');
            ascii.append(stringPad.apply(scores.get(i), scoreWidth));

            ascii.append('|');
            ascii.append('\n');
        }

        // Lower border
        for (int i = 0; i < totalWidth; i++) {
            ascii.append('-');
        }
        ascii.append('\n');

        return ascii.toString();
    }

    /**
     * Adds a given player.
     *
     * @param player The player to add
     */
    public void add(final Player player) {
        add(player.getName());
    }

    /**
     * Adds a given player.
     *
     * @param player The player to add
     */
    public void add(final String player) {
        if (!map.containsKey(player)) {
            map.put(player, new Triple());
        }
    }

    /**
     * Sets the current prediction of a given player.
     *
     * @param player The player to set the current prediction of
     * @param prediction The prediction to set
     */
    public void setPredictions(final Player player, int prediction) {
        setPredictions(player.getName(), prediction);
    }

    /**
     * Sets the current prediction of a given player.
     *
     * @param player The player to set the current prediction of
     * @param prediction The prediction to set
     */
    public void setPredictions(final String player, int prediction) {
        Triple triple = map.get(player);
        if (triple == null) {
            add(player);
        }
        triple.prediction = prediction;
    }

    /**
     * Sets the current number of taken tricks of a given player.
     *
     * @param player The player to set the current number of tricks of
     * @param tricks The number of tricks to set
     */
    public void setTricks(final Player player, int tricks) {
        setTricks(player.getName(), tricks);
    }

    /**
     * Sets the current number of taken tricks of a given player.
     *
     * @param player The player to set the current number of tricks of
     * @param tricks The number of tricks to set
     */
    public void setTricks(final String player, int tricks) {
        Triple triple = map.get(player);
        if (triple == null) {
            add(player);
        }
        triple.tricks = tricks;
    }

    /**
     * Sets the current score of a given player.
     *
     * @param player The player to set the current score of
     * @param score The score to set
     */
    public void setScore(final Player player, int score) {
        setScore(player.getName(), score);
    }

    /**
     * Sets the current score of a given player.
     *
     * @param player The player to set the current score of
     * @param score The score to set
     */
    public void setScore(final String player, int score) {
        Triple triple = map.get(player);
        if (triple == null) {
            add(player);
        }
        triple.score = score;
    }

    /**
     * Increases the current number of taken tricks of a given player by one.
     *
     * @param player The player to increase the number of taken tricks of
     */
    public void addTrick(final Player player) {
        addTrick(player.getName());
    }

    /**
     * Increases the current number of taken tricks of a given player by one.
     *
     * @param player The player to increase the number of taken tricks of
     */
    public void addTrick(final String player) {
        int tricks = getTricks(player);
        setTricks(player, tricks + 1);
    }

    /**
     * Increases the current score of a given player by a given amount.
     *
     * @param player The player of which to increase the score
     * @param score The amount of how much to increase the score
     */
    public void addScore(final Player player, int score) {
        addScore(player.getName(), score);
    }

    /**
     * Increases the current score of a given player by a given amount.
     *
     * @param player The player of which to increase the score
     * @param score The amount of how much to increase the score
     */
    public void addScore(final String player, int score) {
        int current = getScore(player);
        setScore(player, current + score);
    }

    /**
     * Returns the current prediction of a given player.
     *
     * @param player The player to return the current prediction of
     * @return The current prediction of given player
     */
    public int getPrediction(final Player player) {
        return getPrediction(player.getName());
    }

    /**
     * Returns the current prediction of a given player.
     *
     * @param player The player to return the current prediction of
     * @return The current prediction of given player
     */
    public int getPrediction(final String player) {
        Triple triple = map.get(player);
        return (triple == null) ? null : triple.prediction;
    }

    /**
     * Returns the current number of taken tricks of a given play)er.
     *
     * @param player The player to return the current tricks of
     * @return The current number of taken tricks of given player
     */
    public int getTricks(final Player player) {
        return getTricks(player.getName());
    }

    /**
     * Returns the current number of taken tricks of a given play)er.
     *
     * @param player The player to return the current tricks of
     * @return The current number of taken tricks of given player
     */
    public int getTricks(final String player) {
        Triple triple = map.get(player);
        return (triple == null) ? null : triple.tricks;
    }

    /**
     * Returns the current score of a given player.
     *
     * @param player The player to return the current score of
     * @return The current score of a given player
     */
    public int getScore(final Player player) {
        return getScore(player.getName());
    }

    /**
     * Returns the current score of a given player.
     *
     * @param player The player to return the current score of
     * @return The current score of a given player
     */
    public int getScore(final String player) {
        Triple triple = map.get(player);
        return (triple == null) ? null : triple.score;
    }

    /**
     * Convert tricks and predictions of players to score.
     * Call this when round ends.
     */
    public void predictionsToScore() {
        for (Entry<String, Triple> entry : map.entrySet()) {
            String player = entry.getKey();
            int prediction = entry.getValue().prediction;
            int tricks = entry.getValue().tricks;
            int score = entry.getValue().score;

            // Calculate achieved score
            int achievedScore = 0;
            if (tricks == prediction) {
                // Correct predictions score 20 points
                // plus 10 points for every taken trick.

                System.out.printf("%s predicted correctly and earned 20 + %2d points\n",
                    player, 10 * tricks);

                achievedScore += 20;
                achievedScore += (10 * tricks);
            } else {
                // Every wrong prediction awards -10 points.

                System.out.printf("%s made a false prediction and lost %2d points\n",
                    player, 10 * Math.abs(tricks - prediction));

                achievedScore -= 10 * Math.abs(tricks - prediction);
            }

            // Apply achieved score to total score
            setScore(player, score + achievedScore);

            // Set prediction to -1 and tricks to 0 to indicate they are
            // not yet set.
            setPredictions(player, -1);
            setTricks(player, 0);
        }
    }
}
