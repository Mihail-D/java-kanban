package controls;

public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
