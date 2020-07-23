package wizard.client;

public class Main {
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        new Thread(new ServerConnection()).start();
    }
}
