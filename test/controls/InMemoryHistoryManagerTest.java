package controls;

import tasks.TaskStatus;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void load() throws IOException {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();

        Task.setIdCounter(0);
        Task Task = new Task(
                "testTask",
                "taskDescription",
                TaskStatus.NEW,
                Instant.now(),
                Duration.ZERO
        );
        taskManager.addTask(Task);
        historyManager.add(Task, 0);
    }

    @Test
    void shouldRemoveTaskFromHistoryList() {
        assertNotNull(historyManager.getHistory().get(0));
        historyManager.removeHistory(taskManager.getTask(1).getTaskKey());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void shouldReturnHistoryListOfTasks() {
        assertNotNull(historyManager.getHistory());
    }

    @Test
    void shouldAddTasksToHistoryList() {
        assertNotNull(historyManager.getHistory());
        assertEquals(1, historyManager.getHistory().size());
    }
}