package controls;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;

    protected Epic epic1;
    protected Epic epic2;
    protected SubTask subtask1;
    protected SubTask subtask2;

    protected void initTasks() {
        task1 = new Task("Task 1", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H110M"),
                LocalDateTime.parse("11.58.08_07.2023", Task.getTaskTimeFormatter())
        );
        task2 = new Task("Task 2", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H90M"),
                LocalDateTime.parse("09.58.08_06.2023", Task.getTaskTimeFormatter())
        );
        task3 = new Task("Task 3", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H60M"),
                LocalDateTime.parse("10.58.08_05.2023", Task.getTaskTimeFormatter())
        );
        epic1 = new Epic("Epic 1", "Epic Description 1", TaskStatus.NEW);
        epic2 = new Epic("Epic 2", "Epic Description 1", TaskStatus.NEW);
        subtask1 = new SubTask("SubTask 1", "SubTask Description 1", TaskStatus.NEW, Duration.parse("PT0H20M"),
                LocalDateTime.parse("14.58.08_04.2023", Task.getTaskTimeFormatter()), epic1.getTaskKey()
        );
        subtask2 = new SubTask("SubTask 2", "SubTask Description 1", TaskStatus.NEW, Duration.parse("PT0H15M"),
                LocalDateTime.parse("13.58.08_04.2023", Task.getTaskTimeFormatter()), epic1.getTaskKey()
        );
    }

    @Test
    void shouldCheckEpicStatus() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic1.getTaskStatus(), "Ошибочный статус эпика (ожидалось NEW");
        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus(),
                "Ошибочный статус эпика (ожидалось IN_PROGRESS"
        );

        subtask2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic1.getTaskStatus(),
                "Ошибочный статус эпика (ожидалось DONE"
        );
        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus(),
                "Ошибочный статус эпика (ожидалось IN_PROGRESS"
        );
    }

    @Test
    void shouldAddEpic() {
        taskManager.addEpic(epic1);
        final Epic savedEpic = taskManager.getEpic(epic1.getTaskKey());

        assertNotNull(savedEpic, "Эпик не обнаружен.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpicsCollection();

        assertNotNull(epics, "Задачи  на возвращаются.");
        assertEquals(1, epics.size(), "Ошибочное количество задач.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task1);
        final Task savedTask = taskManager.getTask(task1.getTaskKey());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasksCollection();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Ошибочное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);

        final SubTask savedSubtask = taskManager.getSubtask(subtask1.getTaskKey());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают.");

        final List<SubTask> subtasks = taskManager.getSubtasksCollection();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Ошибочное количество подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.addEpic(epic1);
        epic1.setTaskDescription("тест");
        taskManager.updateEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1.getTaskKey()), "Задача не найдена.");
        assertEquals(taskManager.getEpic(epic1.getTaskKey()).getTaskDescription(), "тест",
                "Задача не обновляется"
        );
    }

    @Test
    void shouldUpdateTask() {
        taskManager.addTask(task1);
        task1.setTaskDescription("тест");
        taskManager.updateTask(task1);
        assertNotNull(taskManager.getTask(task1.getTaskKey()), "Задача не найдена.");
        assertEquals(taskManager.getTask(task1.getTaskKey()).getTaskDescription(), "тест",
                "Задача не обновляется"
        );
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        subtask1.setTaskDescription("тест");
        taskManager.updateSubtask(subtask1);
        assertNotNull(taskManager.getSubtask(subtask1.getTaskKey()), "Подзадача не найдена.");
        assertEquals(taskManager.getSubtask(subtask1.getTaskKey()).getTaskDescription(),
                "тест", "Подзадача не обновляется"
        );
    }

    @Test
    void shouldRemoveEpic() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.removeEpic(epic1.getTaskKey());
        final List<SubTask> subtasks = taskManager.getSubtasksCollection();
        final List<Epic> epics = taskManager.getEpicsCollection();
        assertNotNull(epics, "Список задач не возвращается.");
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(0, epics.size(), "Эпик не удален.");
        assertEquals(0, subtasks.size(), "Подзадачи не удалены.");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.addTask(task1);
        taskManager.removeTask(task1.getTaskKey());
        final List<Task> tasks = taskManager.getTasksCollection();
        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(0, tasks.size(), "Задача не удалена.");
    }

    @Test
    void shouldRemoveSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        taskManager.clearSubTasks();
        final List<SubTask> subtasks = taskManager.getSubtasksCollection();
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(0, subtasks.size(), "Подзадача не удалена.");
    }

    @Test
    void shouldGetEpic() {
        taskManager.addEpic(epic1);
        final Epic savedEpic = taskManager.getEpic(epic1.getTaskKey());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldGetTask() {
        taskManager.addTask(task1);
        final Task savedTask = taskManager.getTask(task1.getTaskKey());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задача не совпадают.");
    }

    @Test
    void shouldGetSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);

        final SubTask savedSubtask = taskManager.getSubtask(subtask1.getTaskKey());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void shouldGetEpicsCollection() {
        taskManager.addEpic(epic1);
        final List<Epic> epics = taskManager.getEpicsCollection();
        assertNotNull(epics, "Список эпиков не возвращается.");
        assertEquals(1, epics.size(), "Ошибочное количество эпиков");
    }

    @Test
    void shouldGetSubtasksCollection() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        final List<SubTask> tasks = taskManager.getSubtasksCollection();
        assertNotNull(tasks, "Список подзадач не возвращается.");
        assertEquals(1, tasks.size(), "Ошибочное количество подзадач");
    }

    @Test
    void shouldGetTasksCollection() {
        taskManager.addTask(task1);
        final List<Task> tasks = taskManager.getTasksCollection();
        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(1, tasks.size(), "Ошибочное количество задач");
    }

    @Test
    void shouldGetEpicRelatedSubtasks() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        List<SubTask> subtasks = taskManager.getEpicRelatedSubtasks(epic1.getTaskKey());
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(2, subtasks.size(), "Ошибочное количество подзадач.");
    }

    @Test
    void shouldClearEpics() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.clearEpics();
        final List<SubTask> subtasks = taskManager.getSubtasksCollection();
        final List<Epic> epics = taskManager.getEpicsCollection();
        assertNotNull(epics, "Список эпиков не возвращается.");
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(0, epics.size(), "Эпики не удалены.");
        assertEquals(0, subtasks.size(), "Подзадачи не удалены.");
    }

    @Test
    void shouldClearTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.clearTasks();
        final List<Task> tasks = taskManager.getTasksCollection();
        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(0, tasks.size(), "Задачи не удалены.");
    }

    @Test
    void shouldClearSubTasks() {
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.clearSubTasks();
        final List<SubTask> subtasks = taskManager.getSubtasksCollection();
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(0, subtasks.size(), "Подзадачи не удалена.");
    }

    @Test
    void shouldGHistory() {
        List<Task> history;

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getTaskKey());
        taskManager.getTask(task2.getTaskKey());
        taskManager.getTask(task3.getTaskKey());

        history = taskManager.getHistory();
        assertNotNull(history, "Список истории не возвращается");
        assertEquals(3, history.size(), "Ошибочное количество задач в истории");

        taskManager.getTask(task1.getTaskKey());
        history = taskManager.getHistory();
        assertEquals(task1.getTaskKey(), history.get(2).getTaskKey(), "Ошибочная очередность в истории");

        taskManager.removeTask(task1.getTaskKey());
        history = taskManager.getHistory();
        assertEquals(task3.getTaskKey(), history.get(1).getTaskKey(), "Ошибочное удаление задачи из истории в конце");

        taskManager.addTask(task1);
        taskManager.getTask(task1.getTaskKey());
        taskManager.removeTask(task2.getTaskKey());
        history = taskManager.getHistory();
        assertEquals(task3.getTaskKey(), history.get(0).getTaskKey(), "Ошибочное удаление задачи из истории в начале");

        taskManager.addTask(task2);
        taskManager.getTask(task2.getTaskKey());
        taskManager.removeTask(task1.getTaskKey());
        history = taskManager.getHistory();
        assertEquals(task2.getTaskKey(), history.get(1).getTaskKey(), "Ошибочное удаление задачи из истории в " +
                "середине");

        taskManager.removeTask(task2.getTaskKey());
        taskManager.removeTask(task3.getTaskKey());
        history = taskManager.getHistory();
        assertNotNull(history, "Список истории не возвращен");
        assertEquals(0, history.size(), "История не очищена");
    }

    @Test
    void shouldGetPrioritizedTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        final List<Task> tasks = taskManager.getPrioritizedTasks();
        assertNotNull(tasks, "Список отсортированных задач не возвращается.");
        assertEquals(3, tasks.size(), "Ошибочное количество задач.");
        assertEquals(task1, tasks.get(2), "Неверная очередность задач");
    }

    @Test
    void shouldCreateProperKeys() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(1, task2.getTaskKey() - task1.getTaskKey());
        assertEquals(2, task3.getTaskKey() - task1.getTaskKey());
        assertEquals(3, epic1.getTaskKey() - task1.getTaskKey());
        assertEquals(4, subtask1.getTaskKey() - task1.getTaskKey());
        assertEquals(5, subtask2.getTaskKey() - task1.getTaskKey());
    }
}
