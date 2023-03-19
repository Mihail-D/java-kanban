package controls;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void startUp() {
        taskManager = new InMemoryTaskManager();
        initTasks();
    }

}