package controls;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.*;
import static tasks.TaskTypes.SUB_TASK;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File dataFile = new File("./src/data/dataFile.csv");
    File historyFile = new File("./src/data/historyFile.csv");

    @BeforeEach
    public void setTaskManager() throws IOException {
        dataFile.createNewFile();
        historyFile.createNewFile();
        taskManager = new FileBackedTasksManager(dataFile, historyFile);

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
    public void shouldGetInitNumber() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        assertEquals(0, taskManager.getTasksStorage().size());
        taskManager.epicAdd(epic);
        subtask1.setParentId(epic.getTaskId());
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);

        assertEquals(4, taskManager.getTasksStorage().size());
        Task task3 = new Task("task_5", "description_5", false, NEW, TASK,
                LocalDateTime.parse("22.03.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        assertEquals("t.4", task2.getTaskId());
        taskManager.taskAdd(task3);
        assertEquals("t.5", task3.getTaskId());

    }

    @Test
    public void shouldSaveTasks() throws IOException {
        assertEquals(0, Files.size(Path.of("./src/data/dataFile.csv")));

        taskManager.taskAdd(task1);
        String savedTaskKey = task1.getTaskId();
        String restoredTaskKey = null;

        if (dataFile.exists() && !dataFile.isDirectory()) {

            try (final BufferedWriter writer = new BufferedWriter((new FileWriter(dataFile, UTF_8)))) {


                for (String entry : taskManager.getTasksStorage().keySet()) {
                    writer.append(taskManager.getTaskFormattedData(taskManager.getTasksStorage().get(entry).getTaskId()));
                    writer.newLine();
                }

            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить данные задач");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split(",");
                    restoredTaskKey = tokens[0];
                }
            }

            assertEquals(restoredTaskKey, savedTaskKey);
        }
    }

    @Test
    public void shouldSaveEmptyTasks() throws IOException {
        Path path = Path.of("./src/data/dataFile.csv");
        assertEquals(0, Files.size(path));

        if (dataFile.exists() && !dataFile.isDirectory()) {

            try (final BufferedWriter writer = new BufferedWriter((new FileWriter(dataFile, UTF_8)))) {


                for (String entry : taskManager.getTasksStorage().keySet()) {
                    writer.append(taskManager.getTaskFormattedData(taskManager.getTasksStorage().get(entry).getTaskId()));
                    writer.newLine();
                }

            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить данные задач");
            }

            assertEquals(0, Files.size(path));
        }
    }

    @Test
    public void shouldSaveEmptyEpic() throws IOException {
        assertEquals(0, Files.size(Path.of("./src/data/dataFile.csv")));

        taskManager.epicAdd(epic);
        String savedTaskKey = epic.getTaskId();
        String restoredTaskKey = null;

        assertEquals(0, epic.relatedSubTask.size());

        if (dataFile.exists() && !dataFile.isDirectory()) {

            try (final BufferedWriter writer = new BufferedWriter((new FileWriter(dataFile, UTF_8)))) {


                for (String entry : taskManager.getTasksStorage().keySet()) {
                    writer.append(taskManager.getTaskFormattedData(taskManager.getTasksStorage().get(entry).getTaskId()));
                    writer.newLine();
                }

            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить данные задач");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split(",");
                    restoredTaskKey = tokens[0];
                }
            }

            assertEquals(restoredTaskKey, savedTaskKey);
        }
    }

    @Test
    public void shouldSaveHistory() throws IOException {
        assertEquals(0, Files.size(Path.of("./src/data/historyFile.csv")));
        taskManager.taskAdd(task1);
        String savedHistoryKey = task1.getTaskId();
        String restoredHistoryKey = null;
        taskManager.taskRetrieve(task1.getTaskId());

        if (historyFile.exists() && !historyFile.isDirectory()) {

            try (final BufferedWriter writer = new BufferedWriter((new FileWriter(historyFile, UTF_8)))) {


                for (String entry : taskManager.getTasksStorage().keySet()) {
                    writer.append(taskManager.getTaskFormattedData(taskManager.getTasksStorage().get(entry).getTaskId()));
                    writer.newLine();
                }

            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить данные задач");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] tokens = line.split(",");
                restoredHistoryKey = tokens[0];
            }
        }

        assertEquals(restoredHistoryKey, savedHistoryKey);
    }

    @Test
    public void shouldRestoreTasks() throws IOException {
        FileBackedTasksManager newTaskManager = new FileBackedTasksManager(dataFile, historyFile);
        assertEquals(0, Files.size(dataFile.toPath()));
        assertEquals(0, FileBackedTasksManager.getTaskManager().getTasksStorage().size());
        newTaskManager.taskAdd(task1);
        newTaskManager.taskAdd(task2);
        assertNotEquals(0, Files.size(dataFile.toPath()));

        newTaskManager.restoreTasks(dataFile);
        assertEquals(2, FileBackedTasksManager.getTaskManager().getTasksStorage().size());
    }

    @Test
    public void shouldRestoreEmptyTasks() throws IOException {
        FileBackedTasksManager newTaskManager = new FileBackedTasksManager(dataFile, historyFile);

        BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath());
        writer.write("");
        writer.flush();

        assertEquals(0, Files.size(dataFile.toPath()));

        newTaskManager.restoreTasks(dataFile);
        assertEquals(0, FileBackedTasksManager.getTaskManager().getTasksStorage().size());
    }

    @Test
    public void shouldRestoreHistory() throws IOException {
        FileBackedTasksManager newTaskManager = new FileBackedTasksManager(dataFile, historyFile);
        assertEquals(0, Files.size(historyFile.toPath()));
        assertEquals(0, InMemoryHistoryManager.getHistoryStorage().getSize());

        newTaskManager.taskAdd(task1);
        newTaskManager.taskAdd(task2);
        newTaskManager.taskRetrieve(task1.getTaskId());
        newTaskManager.taskRetrieve(task2.getTaskId());

        assertNotEquals(0, Files.size(historyFile.toPath()));

        newTaskManager.restoreTasks(historyFile);
        assertEquals(2, InMemoryHistoryManager.getHistoryStorage().getSize());
    }

    @Test
    public void shouldRestoreEmptyEpic() throws IOException {
        FileBackedTasksManager newTaskManager = new FileBackedTasksManager(dataFile, historyFile);
        assertEquals(0, Files.size(dataFile.toPath()));
        assertEquals(0, FileBackedTasksManager.getTaskManager().getTasksStorage().size());
        newTaskManager.epicAdd(epic);
        assertNotEquals(0, Files.size(dataFile.toPath()));

        newTaskManager.restoreTasks(dataFile);
        assertEquals(1, FileBackedTasksManager.getTaskManager().getTasksStorage().size());
    }

    @AfterEach
    public void deleteFile() throws IOException {
        Path dataPath = Paths.get("./src/data/dataFile.csv");
        Path historyPath = Paths.get("./src/data/historyFile.csv");
        try {
            Files.deleteIfExists(dataPath);
            Files.deleteIfExists(historyPath);
        } catch (IOException ex) {
            throw new IOException("error while file deletion");
        }
    }
}