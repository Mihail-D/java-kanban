package controls;

import org.junit.jupiter.api.BeforeAll;

public class FileBackedTasksManagerTest extends TaskManagerTest {

    static FileBackedTasksManager manager;
    static InMemoryTaskManager taskManager;
    static InMemoryHistoryManager historyManager;

    @BeforeAll
    static void start() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        manager = new FileBackedTasksManager();
    }
}