package controls;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setTaskManager() throws IOException {
        taskManager = new InMemoryTaskManager();
        setUp();
    }
}