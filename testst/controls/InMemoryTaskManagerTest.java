package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
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
                LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), "e.2"
        );
        subtask2 = new SubTask("task_8", "description_8", false, NEW, SUB_TASK,
                LocalDateTime.parse("23.02.2023_08:00", formatter), Duration.ofMinutes(60), "e.2"
        );
    }

    @Test
    void shouldGetTasksStorage() {
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        assertEquals(2, InMemoryTaskManager.getTasksStorage().size());
        taskManager.tasksClear();
        InMemoryTaskManager.getTasksStorage();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldTaskAdd() {
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        task1.setTaskId("t.1");
        task2.setTaskId("t.2");
        assertEquals(2, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldNotTaskAdd() {
        taskManager.taskAdd(null);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        taskManager.taskAdd(epic);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test()
    void shouldAddEpic() {
        taskManager.epicAdd(epic);
        epic.setTaskId("e.1");
        assertEquals(1, InMemoryTaskManager.getTasksStorage().size());
        assertEquals(epic, InMemoryTaskManager.getTasksStorage().get("e.1"));
    }

    @Test()
    void shouldNotAddEpic() {
        taskManager.epicAdd(null);
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        Exception exception = assertThrows(ClassCastException.class, () -> taskManager.epicAdd((Epic) task1));
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldAddSubTask() {
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

        assertTrue(InMemoryTaskManager.getTasksStorage().containsKey("t.1"));

        taskManager.taskUpdate("t.1", null, "newDescription_1", "DONE",
                LocalDateTime.parse("22.02.2023_17:00", formatter), Duration.ofMinutes(59)
        );
        assertFalse(task1.isValueNull());
    }

    @Test
    void shouldEpicUpdate() {
        taskManager.epicAdd(epic);
        taskManager.epicUpdate("e.1", "newEpicTitle", "newEpicDescription");
        assertEquals("newEpicTitle", epic.getTaskTitle(), "заголовки задач не совпадают");
        assertEquals("newEpicDescription", epic.getTaskDescription(), "описания задач не совпадают");
    }

    @Test
    void shouldNotEpicUpdate() {
        taskManager.epicAdd(epic);
        taskManager.epicUpdate("e.1", null, "newEpicDescription"); // TODO
        assertFalse(epic.isValueNull());
    }

    @Test
    void shouldSubTaskUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskUpdate("s.2", "newTitle_1", "newDescription_1", "DONE",
                "e.1", "23.02.2023_06:00", Duration.ofMinutes(59)
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
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskUpdate("s.2", null, "newDescription_1", "DONE",
                null, "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        assertFalse(subtask1.isValueNull());
    }

    @Test
    void shouldTaskRetrieve() {
        String taskReference = "t.1,task_1,description_1,true,NEW,TASK,2023-02-22T17:00,PT1H,2023-02-22T18:00";
        String epicReference = "e.2,task_4,description_4,true,NEW,EPIC";
        String subTaskReference = "s.3,task_7,description_7,true,NEW,SUB_TASK,e.2,2023-02-23T06:00,PT1H," +
                "2023-02-23T07:00";

        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);

        String taskData = taskManager.taskRetrieve("t.1");
        String epicData = taskManager.taskRetrieve("e.2");

        assertEquals(taskReference, taskData, "no");
        assertEquals(epicReference, epicData, "no");

        subtask1.setTaskId("s.3");
        taskManager.subTaskAdd(subtask1);
        String subTaskData = taskManager.taskRetrieve("s.3");

        assertEquals(subTaskReference, subTaskData, "no");
    }

    @Test
    void shouldTaskDelete() {
        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);

        assertEquals(3, InMemoryTaskManager.getTasksStorage().size());

        taskManager.taskDelete(task1.getTaskId());
        assertEquals(2, InMemoryTaskManager.getTasksStorage().size());

        taskManager.taskDelete(subtask1.getTaskId());
        assertEquals(1, InMemoryTaskManager.getTasksStorage().size());

        taskManager.taskDelete(epic.getTaskId());
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldTasksClear() {
        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);

        assertEquals(3, InMemoryTaskManager.getTasksStorage().size());
        taskManager.tasksClear();
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
    }

    @Test
    void shouldCollectAllTasks() {
        List<ArrayList<String>> collectedTasks = taskManager.collectAllTasks();
        assertEquals(0, collectedTasks.get(0).size());
        assertEquals(0, collectedTasks.get(1).size());
        assertEquals(0, collectedTasks.get(2).size());

        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);

        collectedTasks = taskManager.collectAllTasks();

        assertEquals(1, collectedTasks.get(0).size());
        assertEquals(1, collectedTasks.get(1).size());
        assertEquals(2, collectedTasks.get(2).size());
    }

    @Test
    void shouldCollectEpicSubtasks() {
        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);
        List<String> collectedSubTasks = taskManager.collectEpicSubtasks(epic.getTaskId());
        assertEquals(0, collectedSubTasks.size());

        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);

        collectedSubTasks = taskManager.collectEpicSubtasks(epic.getTaskId());

        assertEquals(2, collectedSubTasks.size());

    }

    @Test
    void shouldGetPrioritizedTasks() {
        Set<Task> prioritizedTasks = InMemoryTaskManager.getPrioritizedTasks();
        assertEquals(0, prioritizedTasks.size());
        taskManager.taskAdd(task1);
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        assertEquals(3, prioritizedTasks.size());

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