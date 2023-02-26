package controls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.TASK;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    FileBackedTasksManager taskManager;

    private File dataFile;
    private Path dataPath;
    private Path historyPath;
    private File historyFile;

    @BeforeEach
    void createFileBackedTasksManagerTest() {
        dataPath = Path.of("dataFile.csv");
        dataFile = new File(String.valueOf(dataPath));

        historyPath = Path.of("historyFile.csv");
        historyFile = new File(String.valueOf(historyPath));

        taskManager = new FileBackedTasksManager(dataFile, historyFile);
    }

    @Test
    public void getInitNumber() {
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        //taskManager = new FileBackedTasksManager(dataFile, historyFile);
        System.out.println(InMemoryTaskManager.getTasksStorage());
        assertEquals(5, InMemoryTaskManager.getTasksStorage().size());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        Task task3 = new Task("task_5", "description_5", false, NEW, TASK,
                LocalDateTime.parse("22.03.2023_17:00", formatter), Duration.ofMinutes(60)
        );

       // InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.taskAdd(task3);
        System.out.println("=====================================================");
        System.out.println(InMemoryTaskManager.getTasksStorage());
    }

}