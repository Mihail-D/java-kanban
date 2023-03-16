package server;

import controls.FileBackedTasksManagerTest;
import controls.Managers;
import controls.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest extends FileBackedTasksManagerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private static KVServer kvServer;

    @BeforeAll
    static void load() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void start() throws IOException {
        taskServer = new HttpTaskServer();
        taskServer.startHttpTaskServer();
        taskManager = Managers.getDefault();

        Task task = new Task(
                "taskTitle",
                "taskDescription",
                TaskStatus.NEW,
                Instant.now(),
                Duration.ofMinutes(15)
        );
        taskManager.addTask(task);
        assertNotNull(task);
        assertNotNull(taskServer);
        assertNotNull(taskManager);
    }

    @AfterEach
    void stop() {
        taskServer.stopHttpTaskServer();
    }

    @AfterAll
    static void stopAfterAll() {
        kvServer.stop();
    }

    @Test
    void shouldSaveAndLoad() {
        assertDoesNotThrow(() -> taskManager.save());
        assertDoesNotThrow(() -> taskManager.load());
    }
}