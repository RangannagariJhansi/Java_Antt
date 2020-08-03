package wizard.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code WizardServer} object handling the initial connection of players
 * and then starting the game.
 */
public class WizardServer implements Runnable {

    private static final String[] playerNames = {
        "Alfa",
        "Bravo",
        "Charlie",
        "Delta",
        "Echo",
        "Foxtrot",
        "Golf",
        "Hotel"
        };

    private final int port;
    private final int playerCount;

    /**
     * Create new {@code WizardServer}.
     *
     * @param port The network port to listen on for connections
     * @param playerCount The number of players to wait for connections of
     */
    public WizardServer(int port, int playerCount) {
        this.port = port;
        this.playerCount = playerCount;

        if (playerCount > 6) {
            System.err.printf("Having '%d' players is not supported\n", playerCount);
        }
    }

    /**
     * Waits for all clients to connect. Then starts game logic (including
     * sending of messages)
     */
    @Override
    public void run() {
        Thread.currentThread().setName("Game logic thread");

        // Wait until all players are connected
        List<Player> players = new ArrayList<Player>(playerCount);
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Wizard Server running and waiting for connections...");
            while (players.size() != playerCount) {
                Socket client = server.accept();
                synchronized(players) {
                    ClientConnectionHandler con = new ClientConnectionHandler(client);
                    con.start();

                    Player player = new Player(playerNames[players.size()], con);
                    players.add(player);

                    System.out.printf("New player connected: %s\n", player);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException - Error when waiting for clients to connect!");
            e.printStackTrace();
            return;
        }

        // All players are connected - starting the game

        Game game = new Game(players);
        game.play();
        System.out.println("Game Over!");
    }

}
