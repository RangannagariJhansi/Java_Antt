package wizard.server;

public class Main {
    /**
     * Main function for game server.
     *
     * @param args Command line arguments for application
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        new Thread(new WizardServer()).start();
    }
}
