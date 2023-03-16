import server.HttpTaskServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("старт");

        try {
            HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.startHttpTaskServer();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}