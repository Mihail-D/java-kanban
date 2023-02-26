package controls;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    public void setTaskManager() {
        taskManager = new InMemoryTaskManager();
    }
}