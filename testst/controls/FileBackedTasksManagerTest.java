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

public class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager>  {
    FileBackedTasksManager taskManager;

    private File dataFile = new File("./src/data/dataFile.csv");
    //private Path dataPath;
    //private Path historyPath;
    private File historyFile = new File("./src/data/historyFile.csv");
/*    private dataPath = Path.of("dataFile.csv");
    dataFile = new File(String.valueOf(dataPath));

    historyPath = Path.of("historyFile.csv");
    historyFile = new File(String.valueOf(historyPath));*/

    public void setTaskManager() {
        taskManager = new FileBackedTasksManager(dataFile, historyFile);
    }
/*    @BeforeEach

    void createFileBackedTasksManagerTest() {
        dataPath = Path.of("dataFile.csv");
        dataFile = new File(String.valueOf(dataPath));

        historyPath = Path.of("historyFile.csv");
        historyFile = new File(String.valueOf(historyPath));

        //taskManager = new FileBackedTasksManager(dataFile, historyFile);
    }*/

    @Test
    public void getInitNumber() {
        setTaskManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        assertEquals(0, InMemoryTaskManager.getTasksStorage().size());
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.taskAdd(task1);
        taskManager.taskAdd(task2);
        assertEquals(4, InMemoryTaskManager.getTasksStorage().size());
        Task task3 = new Task("task_5", "description_5", false, NEW, TASK,
                LocalDateTime.parse("22.03.2023_17:00", formatter), Duration.ofMinutes(60)
        );
        assertEquals("t.4", task2.getTaskId());
        taskManager.taskAdd(task3);
        assertEquals("t.5", task3.getTaskId());
    }

}