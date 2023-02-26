package controls;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEachForInMemoryTaskManagerTest() {
        taskManager = new InMemoryTaskManager();
    }
}