package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStages.*;
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

        epic = new Epic("task_4", "description_4", false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        subtask1 = new SubTask("task_7", "description_7", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        subtask2 = new SubTask("task_8", "description_8", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_08:00", formatter), Duration.ofMinutes(60), "e.1"
        );
    }

    @Test
    void shouldGetTasksStorage() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        assertEquals(2, InMemoryTaskManager.getTasksStorage().size());
        taskManager.tasksClear();
        InMemoryTaskManager.getTasksStorage();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
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

    @Test()
    void shouldAddEpic() {
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
    void shouldAddSubTask() {                                                             // TODO
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
    void shouldNotAddSubTask() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.epicAdd(epic);
        epic.setTaskId("e.15");
        taskManager.subTaskAdd(subtask1);
        subtask1.setTaskId("s.2");
        taskManager.subTaskAdd(subtask2);
        subtask2.setTaskId("s.3");
        assertNotEquals(3, InMemoryTaskManager.getTasksStorage().size());
        assertNotEquals(subtask1, InMemoryTaskManager.getTasksStorage().get("s.2"));
        assertNotEquals(subtask2, InMemoryTaskManager.getTasksStorage().get("s.3"));

        taskManager.tasksClear();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());

        taskManager.subTaskAdd(null);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        Exception exception = assertThrows(ClassCastException.class, () -> taskManager.subTaskAdd((SubTask) task1));
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldTaskUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.taskAdd(task1);
        taskManager.taskUpdate("t.1", "newTitle_1", "newDescription_1", "DONE",
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(59)
        );

        assertEquals("newTitle_1", task1.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newDescription_1", task1.getTaskDescription(), "описания задач не совпадают");
        assertEquals(DONE, task1.getTaskStatus(), "статусы не совпадают");
        assertEquals(TASK, task1.getTaskType(), "типы задач не совпадают");
        assertEquals(LocalDateTime.parse("22.02.2023_17:00", formatter), task1.getStartTime(), "начальное время не " +
                "совпадает");
        assertEquals(Duration.ofMinutes(59), task1.getDuration(), "длительности задач не совпадают");



    }

    @Test
    void shouldTaskNotUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.taskAdd(task1);
        taskManager.taskUpdate("t.1", "newTitle_1", "newDescription_1", "DONE",
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(59)
        );
        assertNotEquals("newTitle_2", task1.getTaskTitle(), "выполняются неверные условия");
        assertNotEquals("newDescription_2", task1.getTaskDescription(), "выполняются неверные условия");
        assertNotEquals(IN_PROGRESS, task1.getTaskStatus(), "выполняются неверные условия");
        assertNotEquals(SUB_TASK, task1.getTaskType(), "выполняются неверные условия");
        assertNotEquals(LocalDateTime.parse("22.02.2023_17:01", formatter), task1.getStartTime(),
                "выполняются неверные условия"
        );
        assertNotEquals(Duration.ofMinutes(58), task1.getDuration(), "выполняются неверные условия");
    }



    @Test
    void shouldEpicUpdate() {
        taskManager.epicAdd(epic);
        taskManager.epicUpdate("e.1", "newEpicTitle", "newEpicDescription");
        assertEquals("newEpicTitle", epic.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newEpicDescription", epic.getTaskDescription(), "описания задач не совпадают");

        //assertEquals();
    }

    @Test
    void shouldSubTaskUpdate() {
    }

    @Test
    void shouldTaskRetrieve() {
    }

    @Test
    void shouldTaskDelete() {
    }

    @Test
    void shouldTasksClear() {
    }

    @Test
    void shouldCollectAllTasks() {
    }

    @Test
    void shouldCollectEpicSubtasks() {
    }

    @Test
    void shouldGetPrioritizedTasks() {
    }

    @Test
    void shouldGetId() {
    }

    @Test
    void shouldSetEpicStatus() {
    }

    @Test
    void shouldSetEpicTiming() {
    }

    @Test
    void shouldGetTaskFormattedData() {
    }

    @Test
    void shouldTimeSlotsStorageFill() {
    }

    @Test
    void shouldTimeOverlappingCheck() {
    }
}