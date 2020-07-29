package wizard.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import wizard.common.game.ScoreBoard;

public class WizardServer implements Runnable {

    private static final int PORT = 2000;
    private static final int playerCount = 2;
    private static final String[] playerNames = {
        "Player Alfa",
        "Player Bravo",
        "Player Charlie",
        "Player Delta"
        };

    private final ArrayList<Player> players = new ArrayList<Player>();

    /**
     * Waits for all clients to connect. Then starts game logic (including
     * sending of messages)
     */
    @Override
    public void run() {
        Thread.currentThread().setName("Game Logic and Sending Thread");

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Wizard Server running and waiting for connections...");
            while (players.size() != playerCount) {
                Socket client = server.accept();
                synchronized(players) {
                    PlayerConnectionHandler con = new PlayerConnectionHandler(client);
                    con.start();
                    Player player = new Player(playerNames[players.size()], con);
                    players.add(player);
                    System.out.printf("New player connected: %s\n", player);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException - Error when waiting for clients to connect!");
            e.printStackTrace();
        }

        System.out.println("All players are connected. Starting game...");

        ScoreBoard scoreBoard = new ScoreBoard();
        for (Player player : players) {
            scoreBoard.add(player);
        }

        // TODO: dynamically calculate number of rounds
        for (int r = 1; r < 5; r++) {
            Round round = new Round(r, players, scoreBoard);
            round.play();
        }

        System.out.println("Game Over!");
    }

}
