import server.KVServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("старт");

        try {
            KVServer taskServer = new KVServer();
            taskServer.start();
            taskServer.stop();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}