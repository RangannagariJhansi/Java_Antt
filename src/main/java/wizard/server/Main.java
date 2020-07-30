package wizard.server;

public class Main {

    private static final int PORT = 2000;

    /**
     * Main function for game server.
     *
     * @param args Command line arguments for application
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Main thread");

        WizardServer server = new WizardServer(PORT, 2);
        new Thread(server).start();
    }
}
