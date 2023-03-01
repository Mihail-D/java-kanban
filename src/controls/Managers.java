package controls;

import java.io.IOException;

public class Managers {

    private static TaskManager taskManager;

    static {
        try {
            taskManager = new InMemoryTaskManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
