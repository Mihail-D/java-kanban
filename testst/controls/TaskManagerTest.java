package controls;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStages.*;
import static tasks.TaskTypes.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    public Task task1;
    public Task task2;
    public Epic epic;
    public SubTask subtask1;
    public SubTask subtask2;

    void setUp() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

        epic = new Epic("task_4", "description_4", false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        assertEquals(0, taskManager.collectAllTasks().size());
        task1 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        task2 = new Task("task_2", "description_2", false, NEW, TASK,
                LocalDateTime.parse("22.02.2023_19:00", formatter), Duration.ofMinutes(60)
        );

        subtask1 = new SubTask("task_7", "description_7", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), epic.getTaskId()
        );
        subtask2 = new SubTask("task_8", "description_8", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_08:00", formatter), Duration.ofMinutes(60), epic.getTaskId()
        );
    }

    @Test
    void shouldGetTasksStorage() {
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        assertEquals(2, taskManager.collectAllTasks().size());
        taskManager.tasksClear();
        taskManager.collectAllTasks();
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldTaskAdd() {
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        assertEquals(2, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldNotTaskAdd() {
        taskManager.taskAdd(null);
        assertEquals(0, taskManager.collectAllTasks().size());
        taskManager.taskAdd(epic);
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test()
    void shouldAddEpic() {
        taskManager.epicAdd(epic);
        assertEquals(1, taskManager.collectAllTasks().size());
        assertEquals(epic, taskManager.collectAllTasks().get(epic.getTaskId()));
    }

    @Test()
    void shouldNotAddEpic() {
        taskManager.epicAdd(null);
        assertEquals(0, taskManager.collectAllTasks().size());
        Exception exception = assertThrows(ClassCastException.class, () -> taskManager.epicAdd((Epic) task1));
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldAddSubTask() {                                                           // TODO
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        assertEquals(3, taskManager.collectAllTasks().size());
        assertEquals(subtask1, taskManager.collectAllTasks().get(subtask1.getTaskId()));
        assertEquals(subtask2, taskManager.collectAllTasks().get(subtask2.getTaskId()));
    }

    @Test
    void shouldNotAddSubTask() {
        taskManager.epicAdd(epic);

        taskManager.tasksClear();
        assertEquals(0, taskManager.collectAllTasks().size());

        taskManager.subTaskAdd(null);
        assertEquals(0, taskManager.collectAllTasks().size());
        Exception exception = assertThrows(ClassCastException.class, () -> taskManager.subTaskAdd((SubTask) task1));
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldTaskUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.taskAdd(task1);
        String taskKey = task1.getTaskId();
        taskManager.taskUpdate(taskKey, "newTitle_1", "newDescription_1", "DONE",
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
        String taskKey = task1.getTaskId();
        taskManager.taskUpdate(taskKey, "newTitle_1", "newDescription_1", "DONE",
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

        assertTrue(taskManager.collectAllTasks().containsKey(taskKey));

        taskManager.taskUpdate("t.1", null, "newDescription_1", "DONE",
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(59)
        );
        assertFalse(task1.isValueNull());
    }

    @Test
    void shouldEpicUpdate() {
        taskManager.epicAdd(epic);
        String taskKey = epic.getTaskId();
        taskManager.epicUpdate(taskKey, "newEpicTitle", "newEpicDescription");
        assertEquals("newEpicTitle", epic.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newEpicDescription", epic.getTaskDescription(), "описания задач не совпадают");
    }

    @Test
    void shouldNotEpicUpdate() {
        taskManager.epicAdd(epic);
        String taskKey = epic.getTaskId();
        taskManager.epicUpdate(taskKey, null, "newEpicDescription");
        assertFalse(epic.isValueNull());
    }

    @Test
    void shouldSubTaskUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        String taskKey = subtask1.getTaskId();
        taskManager.subTaskUpdate(taskKey, "newTitle_1", "newDescription_1", "DONE",
                epic.getTaskId(), "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        assertEquals("newTitle_1", subtask1.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newDescription_1", subtask1.getTaskDescription(), "описания задач не совпадают");

        assertEquals(LocalDateTime.parse("23.02.2023_06:00", formatter), subtask1.getStartTime(), "начальное время не " +
                "совпадает");
        assertEquals(Duration.ofMinutes(59), subtask1.getDuration(), "длительности задач не совпадают");
    }

    @Test
    void shouldNotSubTaskUpdate() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskUpdate(subtask1.getTaskId(), null, "newDescription_1", "DONE",
                null, "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        assertFalse(subtask1.isValueNull());
    }

    @Test
    void shouldTaskRetrieve() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        String[] epicData = taskManager.taskRetrieve(epic.getTaskId()).split(",");
        String[] subTaskData = taskManager.taskRetrieve(subtask1.getTaskId()).split(",");
        String[] taskData = taskManager.taskRetrieve(task1.getTaskId()).split(",");

        assertEquals(epic.getTaskId(), epicData[0]);
        assertEquals(task1.getTaskId(), taskData[0]);
        assertEquals(subtask1.getTaskId(), subTaskData[0]);
    }

    @Test
    void shouldTaskDelete() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.taskAdd(task1);
        taskManager.subTaskAdd(subtask1);

        assertEquals(3, taskManager.collectAllTasks().size());

        taskManager.taskDelete(task1.getTaskId());
        assertEquals(2, taskManager.collectAllTasks().size());

        taskManager.taskDelete(subtask1.getTaskId());
        assertEquals(1, taskManager.collectAllTasks().size());

        taskManager.taskDelete(epic.getTaskId());
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldTasksClear() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        assertEquals(3, taskManager.collectAllTasks().size());
        taskManager.tasksClear();
        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldCollectAllTasks() {
        assertEquals(0, taskManager.collectAllTasks().size());

        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.taskAdd(task1);

        assertEquals(4, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldReturnEmptyTasksCollection() {
        assertEquals(0, taskManager.collectAllTasks().size());
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.taskAdd(task1);
        assertEquals(4, taskManager.collectAllTasks().size());

        taskManager.taskDelete(subtask1.getTaskId());
        taskManager.taskDelete(subtask2.getTaskId());
        taskManager.taskDelete(task1.getTaskId());
        taskManager.taskDelete(epic.getTaskId());

        assertEquals(0, taskManager.collectAllTasks().size());
    }

    @Test
    void shouldCollectEpicSubtasks() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.taskAdd(task1);
        List<String> collectedSubTasks = taskManager.collectEpicSubtasks(epic.getTaskId());
        assertEquals(0, collectedSubTasks.size());

        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);

        collectedSubTasks = taskManager.collectEpicSubtasks(epic.getTaskId());

        assertEquals(2, collectedSubTasks.size());
    }

    @Test
    void shouldGetPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(0, prioritizedTasks.size());
        taskManager.epicAdd(epic);
        taskManager.taskAdd(task1);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        assertEquals(3, prioritizedTasks.size());
        assertSame(prioritizedTasks.first(), task1);
        assertSame(prioritizedTasks.last(), subtask2);
    }

    @Test
    void shouldGetNextIdForTask() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        Task task3 = new Task("task_1", "description_1", false, NEW, TASK,
                LocalDateTime.parse("25.02.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        Task task4 = new Task("task_2", "description_2", false, NEW, TASK,
                LocalDateTime.parse("25.02.2023_19:00", formatter), Duration.ofMinutes(60)
        );

        taskManager.taskAdd(task3);
        taskManager.taskAdd(task4);

        int numberIndexForTask3 = Integer.parseInt(task3.getTaskId().substring(2));
        int numberIndexForTask4 = Integer.parseInt(task4.getTaskId().substring(2));

        assertEquals(1, Integer.parseInt(task3.getTaskId().substring(2)));
        assertEquals(2, Integer.parseInt(task4.getTaskId().substring(2)));

        int predicateIndexForNexTask = numberIndexForTask4 + 1;

        Epic epic1 = new Epic("task_4", "description_4", false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        taskManager.epicAdd(epic1);
        int numberIndexForEpic1 = Integer.parseInt(epic1.getTaskId().substring(2));

        assertEquals(predicateIndexForNexTask, numberIndexForEpic1);
    }

    @Test
    void shouldSetEpicStatus() {
        taskManager.epicAdd(epic);
        assertSame(epic.getTaskStatus(), NEW);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskUpdate(subtask1.getTaskId(), "newTitle_1", "newDescription_1",
                "DONE", epic.getTaskId(), "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        assertSame(epic.getTaskStatus(), IN_PROGRESS);

        taskManager.subTaskUpdate(subtask2.getTaskId(), "newTitle_2", "newDescription_2",
                "DONE", epic.getTaskId(), "23.02.2023_08:00", Duration.ofMinutes(59)
        );
        assertSame(epic.getTaskStatus(), DONE);
        taskManager.taskDelete(subtask1.getTaskId());
        taskManager.taskDelete(subtask2.getTaskId());

        assertSame(epic.getTaskStatus(), NEW);
    }

    @Test
    void shouldSetEpicTiming() {
        taskManager.epicAdd(epic);
        assertSame(epic.getStartTime(), LocalDateTime.MAX);
        assertSame(epic.getDuration(), Duration.ZERO);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);

        assertTrue(subtask1.getStartTime().isBefore(subtask2.getStartTime()));
        assertTrue(subtask1.getEndTime().isBefore(subtask2.getEndTime()));

        assertSame(epic.getStartTime(), subtask1.getStartTime());
        assertEquals(epic.getDuration(), subtask1.getDuration().plus(subtask2.getDuration()));
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
    }

    @Test
    void shouldGetTaskFormattedData() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);

        String[] epicData = taskManager.taskRetrieve(epic.getTaskId()).split(",");
        String[] subTaskData = taskManager.taskRetrieve(subtask1.getTaskId()).split(",");
        String[] taskData = taskManager.taskRetrieve(task1.getTaskId()).split(",");

        assertEquals(epic.getTaskId(), epicData[0]);
        assertEquals(task1.getTaskId(), taskData[0]);
        assertEquals(subtask1.getTaskId(), subTaskData[0]);
    }

    @Test
    void shouldNotThrowTimeOverlapping() {
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        subtask2.setParentId(epic.getTaskId());
        assertDoesNotThrow(() -> taskManager.subTaskAdd(subtask1));
        assertDoesNotThrow(() -> taskManager.subTaskAdd(subtask2));

        taskManager.taskAdd(task1);
        assertDoesNotThrow(() -> taskManager.taskAdd(task2));
    }

    @Test
    void shouldTimeOverlappingCheck() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);

        ManagerSaveException isTask2StartAfterEndBefore = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_17:15", formatter), Duration.ofMinutes(30)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartAfterEndAfter = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_17:15", formatter), Duration.ofMinutes(130)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartBeforeEndBefore = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_16:50", formatter), Duration.ofMinutes(30)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartBeforeEndAfter = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_16:50", formatter), Duration.ofMinutes(130)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartEqualsEndEquals = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(60)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartBeforeEndEquals = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_16:00", formatter), Duration.ofMinutes(60)
                ), "ожидалось ManagerSaveException");

        ManagerSaveException isTask2StartEqualsEndAfter = Assertions.assertThrows(ManagerSaveException.class, () ->
                taskManager.taskUpdate("t.2", "newTitle", "newDescription",
                        "NEW", LocalDateTime.parse("22.02.2023_18:00", formatter), Duration.ofMinutes(60)
                ), "ожидалось ManagerSaveException");
    }
}