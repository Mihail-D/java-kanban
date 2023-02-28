package controls;

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
import java.util.Arrays;
import java.util.LinkedHashMap;

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
        String savedTaskKey = null;
        String restoredTaskKey = null;

        if (dataFile.exists() && !dataFile.isDirectory()) {

            try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split(",");
                    savedTaskKey = tokens[0];
                }
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
        public void shouldSaveHistory () throws IOException {
            assertEquals(0, Files.size(Path.of("./src/data/historyFile.csv")));
            taskManager.taskAdd(task1);
            String savedHistoryKey = null;
            String restoredHistoryKey = null;
            taskManager.taskRetrieve(task1.getTaskId());

            if (historyFile.exists() && !historyFile.isDirectory()) {

                try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        String[] tokens = line.split(",");
                        savedHistoryKey = tokens[0];
                    }
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
        public void shouldRestoreTasks () throws IOException {
            /*Path path = Path.of("./src/data/dataFile.csv");
            Files.deleteIfExists(path);
            assertEquals(0, Files.size(path));*/

            taskManager.taskAdd(task1);
            taskManager.taskAdd(task2);
            assertNotEquals(0, Files.size(dataFile.toPath()));  // 160kb

            FileBackedTasksManager newTaskManager = new FileBackedTasksManager(dataFile, historyFile);
            //InMemoryTaskManager newInMemoryTaskManager = new InMemoryTaskManager();


            assertEquals(2, newTaskManager.getTasksStorage().size());

        }





        @AfterEach
        public void deleteFile () throws IOException {
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