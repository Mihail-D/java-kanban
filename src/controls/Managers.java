package controls;

public class Managers {
    private static TaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {

        return taskManager;
    }

}
