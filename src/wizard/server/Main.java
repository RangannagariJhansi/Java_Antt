package wizard.server;

public class Main {
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        new Thread(new WizardServer()).start();
    }
}
