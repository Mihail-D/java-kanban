package controls;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public String filePath;

    @BeforeEach
    void StartUp() {
        filePath = "test_" + System.nanoTime()+".csv";
        taskManager = new FileBackedTasksManager(filePath);
        initTasks();
    }

    @AfterEach
    public void clearFile() {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldLoad() {
        taskManager = FileBackedTasksManager.load(Paths.get(filePath).toFile());
        List<Task> tasks = taskManager.getTasksCollection();
        assertNotNull(tasks, "Ошибка при обработке пустого файла.");
        assertEquals(0, tasks.size(), "Список задач не пуст при загрузке из пустого файла.");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager = FileBackedTasksManager.load(Paths.get(filePath).toFile());
        List<Task> history = taskManager.getHistory();
        tasks = taskManager.getTasksCollection();
        assertNotNull(tasks, "Ошибка при загрузке из файла");
        assertNotNull(history, "Ошибка при загрузке из файла");
        assertEquals(2, tasks.size(), "Список задач пуст при загрузке из файла.");
        assertEquals(0, history.size(), "История задач не пуста");

        taskManager.getTask(task1.getTaskKey());
        taskManager.getTask(task2.getTaskKey());
        taskManager = FileBackedTasksManager.load(Paths.get(filePath).toFile());
        history = taskManager.getHistory();
        assertNotNull(history, "Ошибка при загрузке из файла");
        assertEquals(2, history.size(), "История задач пустая");
    }
}