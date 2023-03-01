package controls;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setTaskManager() {
        taskManager = new InMemoryTaskManager();
        setUp();
    }
}