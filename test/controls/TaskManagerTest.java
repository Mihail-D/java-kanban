package controls;

import exceptions.ManagerTaskTimeOverlappingException;
import tasks.TaskStatus;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {

    static InMemoryHistoryManager historyManager;
    static InMemoryTaskManager taskManager;

    private static final String testedTaskTitle = "testedTaskTitle";
    private static final String testedTaskDescription = "testedTaskDescription";
    private static final Instant testStartTime = Instant.now();
    private static final Duration testDuration = Duration.ofHours(1);

    static Task testTask;
    static SubTask testSubTask;
    static Epic testEpicTask;

    @BeforeEach
    void initAndCreate() throws IOException {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        Task.setIdCounter(0);

        testTask = new Task(testedTaskTitle, testedTaskDescription, TaskStatus.NEW,
                testStartTime.plusSeconds(60 * 60 * 5), testDuration
        );

        testSubTask = new SubTask(testedTaskTitle, testedTaskDescription, TaskStatus.NEW,
                testStartTime.plusSeconds(60 * 60 * 10), testDuration
        );
        testEpicTask = new Epic(testedTaskTitle, testedTaskDescription, TaskStatus.NEW,
                testStartTime.plusSeconds(60 * 60 * 8), testDuration
        );

        taskManager.addTask(testTask);
        taskManager.addEpic(testEpicTask);
        taskManager.addSubTask(testSubTask, testEpicTask.getTaskKey());
    }

    @Test
    void shouldGetProperSubTask() {
        assertEquals(testSubTask, taskManager.getSubTask(3), "getSubTask() вернул другой результат");
        assertNotNull(taskManager.getSubTask(3));
        assertNull(taskManager.getSubTask(0));
        taskManager.getSubTasksMap().clear();
        assertNull(taskManager.getSubTask(3));
    }

    @Test
    void shouldGetProperTask() {
        assertEquals(testTask, taskManager.getTask(1), "getTask() вернул другой результат");
        assertNotNull(taskManager.getTask(1));
        assertNull(taskManager.getTask(0));
        taskManager.getTasksMap().clear();
        assertNull(taskManager.getTask(1));
    }

    @Test
    void shouldGetProperEpic() {
        assertEquals(testEpicTask, taskManager.getEpic(2), "getEpic() вернул другой результат");
        assertNotNull(taskManager.getEpic(2));
        assertNull(taskManager.getEpic(0));
        taskManager.getSubTasksMap().clear();
        assertNull(taskManager.getEpic(3));
    }

    @Test
    void shouldDeleteTask() throws IOException {
        assertNotNull(taskManager.getTask(1), "Задача не создана");
        taskManager.deleteTask(testTask.getTaskKey());
        assertEquals(0, taskManager.getTasksMap().size(), "Задача не удалена");
    }

    @Test
    void shouldCheckTimeOverlapping() {
        Task tmpTaskBad = new Task(testedTaskTitle, testedTaskDescription, TaskStatus.NEW,
                testStartTime.plusSeconds(60 * 60 * 6), testDuration
        );

        assertThrows(ManagerTaskTimeOverlappingException.class, () -> taskManager.addTask(tmpTaskBad));

        Task tmpTaskGood = new Task(testedTaskTitle, testedTaskDescription, TaskStatus.NEW,
                testStartTime.plusSeconds(60 * 60 * 25), testDuration
        );
        assertTrue(taskManager.advancedTimeOverlappingCheck(tmpTaskGood));
    }

    @Test
    void shouldDeleteSubTask() {
        assertNotNull(taskManager.getSubTask(3), "Задача не создана");
        assertEquals(2, taskManager.getSubTask(3).getParentKey(), "Ошибочный parentKey");
        assertEquals(1, taskManager.getEpic(2).getRelatedSubTasks().size(),
                "Не верный список " + "relatedSubtasks"
        );
        taskManager.deleteSubTask(3);
        assertEquals(0, taskManager.getSubTasksMap().size(), "Задача не удалена");
        assertTrue(
                taskManager.getEpic(2).getRelatedSubTasks().isEmpty(),
                "родитель не удалил ключ подзадачи"
        );
    }

    @Test
    void shouldCreateEpicAndSubTask() {
        assertNotNull(taskManager.getTask(1), "Задача не создана");
        assertNotNull(taskManager.getEpic(2), "Задача не создана");
        assertNotNull(taskManager.getSubTask(3), "Задача не создана");
        assertEquals(testTask, taskManager.getTask(1), "Задача создана неправильно");
        assertEquals(testSubTask, taskManager.getSubTask(3), "Задача создана неправильно");
        assertEquals(testEpicTask, taskManager.getEpic(2), "Задача создана неправильно");
    }

    @Test
    void shouldDeleteEpic() throws IOException {
        assertNotNull(taskManager.getEpic(2), "Задача не создана");
        taskManager.deleteEpic(2);
        taskManager.clearRelatedSubTusks(2);
        assertEquals(0, taskManager.getEpicsMap().size(), "Задача не удалена");
        assertNull(taskManager.getSubTask(3), "подзадача не удалена с родителем");
    }

    @Test
    void shouldDeleteRelatedSubTasks() throws IOException {
        assertNotNull(taskManager.getSubTask(3), "Задача не создана");
        assertNotNull(taskManager.getEpic(2), "Задача не создана");
        taskManager.clearRelatedSubTusks(2);
        assertNotNull(taskManager.getEpic(2), "неправильно удален эпик");
        assertNull(taskManager.getSubTask(3), "подзадача не удалена вместе с родителем");
    }

    @Test
    void shouldDeleteAllTasks() {
        assertNotNull(taskManager.getTask(1), "Задача не создана");
        assertNotNull(taskManager.getEpic(2), "Задача не создана");
        assertNotNull(taskManager.getSubTask(3), "Задача не создана");
        assertNotNull(taskManager.getPrioritizedTasksSet());
        taskManager.getTasksMap().clear();
        taskManager.getSubTasksMap().clear();
        taskManager.getEpicsMap().clear();
        taskManager.getPrioritizedTasksSet().clear();
        assertTrue(taskManager.getTasksMap().isEmpty(), "Задачи не удалены");
        assertTrue(taskManager.getSubTasksMap().isEmpty(), "Задачи не удалены");
        assertTrue(taskManager.getEpicsMap().isEmpty(), "Задачи не удалены");
        assertTrue(taskManager.getPrioritizedTasksSet().isEmpty());
    }

    @Test
    void shouldUpdateEpicStatus() throws IOException {
        SubTask testSubTask2 = new SubTask("testedTaskTitle", "testedTaskDescription",
                TaskStatus.NEW, Instant.now().plusSeconds(60 * 60 * 13), Duration.ofHours(1)
        );
        SubTask testSubTask3 = new SubTask("testedTaskTitle", "testedTaskDescription",
                TaskStatus.NEW, Instant.now().plusSeconds(60 * 60 * 15), Duration.ofHours(1)
        );
        taskManager.addSubTask(testSubTask2, testEpicTask.getTaskKey());
        taskManager.addSubTask(testSubTask3, testEpicTask.getTaskKey());
        assertNotNull(taskManager.getEpic(2), "Задача не создана");

        taskManager.setEpicStatus(2);
        assertEquals(TaskStatus.NEW, taskManager.getEpic(2).getTaskStatus(),
                "Ложный статус при создании"
        );

        taskManager.getSubTask(testSubTask.getTaskKey()).setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.setEpicStatus(2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getTaskStatus(),
                "Ложный статус"
        );

        taskManager.getSubTask(testSubTask.getTaskKey()).setTaskStatus(TaskStatus.DONE);
        taskManager.getSubTask(testSubTask2.getTaskKey()).setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.setEpicStatus(2);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(2).getTaskStatus(),
                "Ложный статус"
        );

        taskManager.getSubTask(testSubTask.getTaskKey()).setTaskStatus(TaskStatus.DONE);
        taskManager.getSubTask(testSubTask2.getTaskKey()).setTaskStatus(TaskStatus.DONE);
        taskManager.getSubTask(testSubTask3.getTaskKey()).setTaskStatus(TaskStatus.DONE);
        taskManager.setEpicStatus(2);
        assertEquals(TaskStatus.DONE, taskManager.getEpic(2).getTaskStatus(),
                "Ложный статус"
        );
    }

    @Test
    void shouldReturnProperPrioritizedTasks() {
        assertEquals(testTask, taskManager.getPrioritizedTasks().get(0));
        assertEquals(testSubTask, taskManager.getPrioritizedTasks().get(1));
    }

    @Test
    void shouldGetPrioritizedTasks() {
        assertNotNull(taskManager.getPrioritizedTasks());
        assertTrue(taskManager.getPrioritizedTasks().get(0).getTaskStartTime()
                .isBefore(taskManager.getPrioritizedTasks().get(1).getTaskStartTime()));
    }
}