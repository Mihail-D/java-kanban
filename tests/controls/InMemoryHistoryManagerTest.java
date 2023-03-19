package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    Task task;
    Task task2;

    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.parse("PT0H120M"),
                LocalDateTime.parse("15.28.08_05.2023", Task.getTaskTimeFormatter())
        );
        task2 = new Task("Task 2", "Description 2", TaskStatus.NEW, Duration.parse("PT0H100M"));
    }

    @Test
    void shouldAdd() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории не возвращается");
        assertEquals(1, history.size(), "История пустая, задача не добавилась.");
    }

    @Test
    void shouldRemove() {
        historyManager.add(task);
        historyManager.remove(task.getTaskKey());
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории не возвращается");
        assertEquals(0, history.size(), "История не пустая, задача не удалилась.");
    }

    @Test
    void shouldGetHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории не возвращается");
        assertEquals(1, history.size(), "История задач пустая.");
    }
}