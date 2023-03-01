package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.*;

class HistoryManagerTest<T extends HistoryManager> {

    InMemoryTaskManager taskManager;
    InMemoryHistoryManager historyManager;

    public Task task1;
    public Task task2;
    public Task task3;
    public Epic epic;
    public SubTask subtask1;

    @BeforeEach
    void setup() throws IOException {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());
        assertEquals(0, InMemoryHistoryManager.getHistoryReport().size());

        epic = new Epic("task_4", "description_4", false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        assertEquals(0, taskManager.getTasksStorage().size());
        task1 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        subtask1 = new SubTask("task_7", "description_7", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        task2 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_21:00", formatter), Duration.ofMinutes(60)
        );
        task3 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_19:00", formatter), Duration.ofMinutes(60)
        );
    }

    @Test
    void shouldAddHistory() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        task1 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(60)
        );

        taskManager.epicAdd(epic);

        subtask1 = new SubTask("task_7", "description_7", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), epic.getTaskId()
        );
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        historyManager.addHistory(epic);
        historyManager.addHistory(subtask1);
        historyManager.addHistory(task1);

        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(3, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldAddEmptyHistory() {
        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());
        historyManager.addHistory(null);
        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldNotAddDoubleHistory() {
        taskManager.taskAdd(task1);
        historyManager.addHistory(task1);

        assertEquals(1, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(1, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.addHistory(task1);
        historyManager.addHistory(task1);
        historyManager.addHistory(task1);

        assertEquals(1, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(1, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldRemoveHistoryRecord() {
        taskManager.taskAdd(task1);
        taskManager.taskRetrieve(task1.getTaskId());
        assertEquals(1, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(1, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.removeHistoryRecord(task1.getTaskId());

        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());
        assertEquals(0, InMemoryHistoryManager.getHistoryReport().size());
    }

    @Test
    void shouldRemoveHistoryRecordFirstElement() {

        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        taskManager.taskAdd(task3);

        taskManager.taskRetrieve(task1.getTaskId());
        taskManager.taskRetrieve(task2.getTaskId());
        taskManager.taskRetrieve(task3.getTaskId());
        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(3, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.removeHistoryRecord(task1.getTaskId());

        assertEquals(2, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(2, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldRemoveHistoryRecordSecondElement() {

        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        taskManager.taskAdd(task3);

        taskManager.taskRetrieve(task1.getTaskId());
        taskManager.taskRetrieve(task2.getTaskId());
        taskManager.taskRetrieve(task3.getTaskId());
        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(3, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.removeHistoryRecord(task2.getTaskId());

        assertEquals(2, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(2, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldRemoveHistoryRecordThirdElement() {

        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        taskManager.taskAdd(task3);

        taskManager.taskRetrieve(task1.getTaskId());
        taskManager.taskRetrieve(task2.getTaskId());
        taskManager.taskRetrieve(task3.getTaskId());
        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(3, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.removeHistoryRecord(task3.getTaskId());

        assertEquals(2, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(2, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldClearHistoryStorage() {
        historyManager.clearHistoryStorage();

        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());

        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        historyManager.addHistory(epic);
        historyManager.addHistory(subtask1);
        historyManager.addHistory(task1);

        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(3, InMemoryHistoryManager.getHistoryRegister().size());

        historyManager.clearHistoryStorage();

        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());
        assertEquals(0, InMemoryHistoryManager.getHistoryRegister().size());
    }

    @Test
    void shouldGetHistory() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        historyManager.addHistory(epic);
        historyManager.addHistory(subtask1);
        historyManager.addHistory(task1);
        assertNotNull(InMemoryHistoryManager.getHistoryStorage().getTasks());
        assertEquals(3, InMemoryHistoryManager.getHistoryStorage().getTasks().size());
    }

    @Test
    void shouldGetHistoryRegister() {
        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getTasks().size());
        taskManager.taskAdd(task1);
        historyManager.addHistory(task1);
        assertNotNull(InMemoryHistoryManager.getHistoryStorage().getTasks());
        assertEquals(1, InMemoryHistoryManager.getHistoryStorage().getTasks().size());
    }

    @Test
    void shouldGetHistoryStorage() {
        InMemoryHistoryManager.CustomLinkedList testHistory = null;
        assertNull(testHistory);
        taskManager.taskAdd(task1);
        historyManager.addHistory(task1);
        assertEquals(1, InMemoryHistoryManager.getHistoryStorage().getTasks().size());
        testHistory = InMemoryHistoryManager.getHistoryStorage();
        assertNotNull(testHistory);
    }
}