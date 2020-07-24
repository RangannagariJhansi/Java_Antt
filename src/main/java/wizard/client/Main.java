package wizard.client;

public class Main {

    /**
     * Main function for game client.
     *
     * @param args Command line arguments for application
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        new Thread(new ServerConnection()).start();
    }
}
