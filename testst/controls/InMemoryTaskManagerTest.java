package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStages.DONE;
import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.*;

class InMemoryTaskManagerTest<T extends TaskManager> {
    public TaskManager taskManager;

    public Task task1;
    public Task task2;
    public Epic epic;
    public SubTask subtask1;
    public SubTask subtask2;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        task1 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        task2 = new Task("task_2", "description_2", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_19:00", formatter), Duration.ofMinutes(60)
        );

        epic = new Epic("task_4", "description_4",false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        subtask1 = new SubTask("task_7", "description_7",false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        subtask2 = new SubTask("task_8", "description_8",false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_08:00", formatter), Duration.ofMinutes(60), "e.1"
        );
    }

    @Test
    void getTasksStorage() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
    }

    @Test
    void testTaskAdd() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        task1.setTaskId("t.1");
        task2.setTaskId("t.2");
        assertEquals(2, InMemoryTaskManager.getTasksStorage().size());
        taskManager.tasksClear();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(null);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(epic);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test ()
    void testEpicAdd() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.epicAdd(epic);
        epic.setTaskId("e.1");
        assertEquals(1, InMemoryTaskManager.getTasksStorage().size());
        assertEquals(epic, InMemoryTaskManager.getTasksStorage().get("e.1"));
        taskManager.tasksClear();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.epicAdd(null);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        Exception exception = assertThrows(ClassCastException.class, () -> taskManager.epicAdd((Epic) task1));
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void testSubTaskAdd() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.epicAdd(epic);
        epic.setTaskId("e.1");
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        subtask1.setTaskId("s.2");
        subtask2.setTaskId("s.3");
        assertEquals(3, InMemoryTaskManager.getTasksStorage().size());
        assertEquals(subtask1, InMemoryTaskManager.getTasksStorage().get("s.2"));
        assertEquals(subtask2, InMemoryTaskManager.getTasksStorage().get("s.3"));
    }

    @Test
    void testTaskUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.taskAdd(task1);
        taskManager.taskUpdate("t.1", "newTitle_1","newDescription_1", "DONE",
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(59));
        assertEquals("newTitle_1", task1.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newDescription_1", task1.getTaskDescription(), "описания задач не совпадают");
        assertEquals(DONE, task1.getTaskStatus(), "статусы не совпадают");
        assertEquals(DONE, task1.getTaskStatus(), "статусы не совпадают");
        assertEquals(LocalDateTime.parse("22.02.2023_17:00", formatter), task1.getStartTime(), "начальное время не " +
                "совпадает");
    }

    @Test
    void testEpicUpdate() {
    }

    @Test
    void testSubTaskUpdate() {
    }

    @Test
    void testTaskRetrieve() {
    }

    @Test
    void testTaskDelete() {
    }

    @Test
    void testTasksClear() {
    }

    @Test
    void testCollectAllTasks() {
    }

    @Test
    void testCollectEpicSubtasks() {
    }

    @Test
    void getPrioritizedTasks() {
    }

    @Test
    void getId() {
    }

    @Test
    void setEpicStatus() {
    }

    @Test
    void setEpicTiming() {
    }

    @Test
    void getTaskFormattedData() {
    }

    @Test
    void timeSlotsStorageFill() {
    }

    @Test
    void advancedTimeOverlappingCheck() {
    }
}