package tasks;

import controls.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tasks.Task.getTaskTimeFormatter;
import static tasks.TaskStatus.*;

class EpicTest {

    public String filePath;
    FileBackedTasksManager manager;
    Epic epic1;
    SubTask subtask1;
    SubTask subtask2;

    @BeforeEach
    void startUp() {
        filePath = "test_" + System.nanoTime() + ".csv";
        manager = new FileBackedTasksManager(filePath);

        epic1 = new Epic("Epic 1", "Epic Description 1", NEW);

        assertNotNull(epic1.getRelatedSubTasks(), "Ошибка при обработке пустого файла.");
        assertEquals(0, epic1.getRelatedSubTasks().size(), "Список задач не пуст при загрузке из пустого файла.");

        subtask1 = new SubTask("SubTask 1", "SubTask Description 1", NEW, Duration.parse("PT0H20M"),
                LocalDateTime.parse("14.58.08_04.2023", getTaskTimeFormatter()), epic1.getTaskKey()
        );
        subtask2 = new SubTask("SubTask 2", "SubTask Description 1", NEW, Duration.parse("PT0H15M"),
                LocalDateTime.parse("13.58.08_04.2023", getTaskTimeFormatter()), epic1.getTaskKey()
        );

    }

    @AfterEach
    public void shouldClearFile() {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldAddChild() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertNotNull(epic1.getRelatedSubTasks(), "Ошибка при загрузке из файла");
        assertEquals(2, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");
    }

    @Test
    void shouldRemoveSubtask() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertNotNull(epic1.getRelatedSubTasks(), "Ошибка при загрузке из файла");
        assertEquals(2, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");

        epic1.removeSubtask(subtask2.getTaskKey());

        assertEquals(1, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");

        epic1.removeSubtask(subtask1.getTaskKey());

        assertEquals(0, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");
    }

    @Test
    void shouldGetRelatedSubTasks() {
        manager.addEpic(epic1);

        assertNotNull(epic1.getRelatedSubTasks(), "Ошибка при загрузке из файла");
        assertEquals(0, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");

        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(2, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");
    }

    @Test
    void shouldSetEndTime() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        manager.addSubtask(subtask1);
        assertEquals(subtask1.getTaskEndTime(), epic1.getTaskEndTime(), "Данные времени не совпадают.");

    }

    @Test
    public void shouldReturnEmptyList() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        manager.addSubtask(subtask1);

        assertNotNull(epic1.getRelatedSubTasks(), "Ошибка при загрузке из файла");
        assertEquals(1, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");

        epic1.removeSubtask(subtask1.getTaskKey());

        assertEquals(0, epic1.getRelatedSubTasks().size(), "Список задач пуст при загрузке из файла.");
    }

    @Test
    public void shouldAllSubtasksAreNew() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(NEW, epic1.getTaskStatus());
        assertEquals(NEW, subtask1.getTaskStatus());
        assertEquals(NEW, subtask2.getTaskStatus());
    }

    @Test
    public void shouldAllSubtasksAreDone() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask1.setTaskStatus(DONE);
        subtask2.setTaskStatus(DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        assertEquals(DONE, epic1.getTaskStatus());
        assertEquals(DONE, subtask1.getTaskStatus());
        assertEquals(DONE, subtask2.getTaskStatus());
    }

    @Test
    public void shouldSubtasksAreNewDone() {
        manager.addEpic(epic1);
        subtask1.setParentKey(epic1.getTaskKey());
        subtask2.setParentKey(epic1.getTaskKey());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        subtask2.setTaskStatus(DONE);
        manager.updateSubtask(subtask2);
        assertEquals(IN_PROGRESS, epic1.getTaskStatus());
        assertEquals(NEW, subtask1.getTaskStatus());
        assertEquals(DONE, subtask2.getTaskStatus());
    }
}