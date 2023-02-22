import controls.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.*;

public class Main {

    static InMemoryTaskManager taskManager;
    static InMemoryHistoryManager inMemoryHistoryManager;
    static FileBackedTasksManager fileBackedTasksManager;

    public static void main(String[] args) throws IOException {
        String PATH = "./src/data";
        Files.createDirectories(Paths.get(PATH));

        File dataFile = new File(PATH + File.separator + "dataFile.csv");
        dataFile.createNewFile();
        File historyFile = new File(PATH + File.separator + "historyFile.csv");
        historyFile.createNewFile();

        fileBackedTasksManager = new FileBackedTasksManager(dataFile, historyFile);
        taskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();

        getControlOptions();
    }

    public static void getControlOptions() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        Scanner scanner = new Scanner(System.in);
        int item;
        String taskTitle;
        String taskDescription;
        String taskKey;
        String parentKey;
        String mode;
        String taskStatus;
        String time;
        String duration;
        String startTime;

        while (true) {
            System.out.println("MENU"); // TODO
            item = scanner.nextInt();

            switch (item) {
                case 1:

                    fileBackedTasksManager.taskAdd(new Task("task_1", "description_1",
                            false, NEW, TASK, LocalDateTime.parse("22.02.2023_17:00", formatter),
                            Duration.ofMinutes(60)
                    ));
                    fileBackedTasksManager.taskAdd(new Task("task_2", "description_2",
                            false, NEW, TASK, LocalDateTime.parse("22.02.2023_19:00", formatter),
                            Duration.ofMinutes(60)
                    ));
                    fileBackedTasksManager.taskAdd(new Task("task_3", "description_3",
                            false, NEW, TASK, LocalDateTime.parse("22.02.2023_21:00", formatter),
                            Duration.ofMinutes(60)
                    ));

                    fileBackedTasksManager.epicAdd(new Epic("task_4", "description_4",
                            false, NEW, EPIC, LocalDateTime.MAX, Duration.ZERO,
                            new LinkedHashMap<>()
                    ));
                    fileBackedTasksManager.epicAdd(new Epic("task_5",
                            "description_5", false, NEW, EPIC, LocalDateTime.MAX, Duration.ZERO,
                            new LinkedHashMap<>()
                    ));
                    fileBackedTasksManager.epicAdd(new Epic("task_6", "description_6",
                            false, NEW, EPIC, LocalDateTime.MAX, Duration.ZERO,
                            new LinkedHashMap<>()
                    ));

                    fileBackedTasksManager.subTaskAdd(new SubTask("task_7", "description_7",
                            false, NEW, SUB_TASK, LocalDateTime.parse("23.02.2023_06:00", formatter),
                            Duration.ofMinutes(60), "e.4"
                    ));
                    fileBackedTasksManager.subTaskAdd(new SubTask("task_8", "description_8",
                            false, NEW, SUB_TASK, LocalDateTime.parse("23.02.2023_08:00", formatter),
                            Duration.ofMinutes(60), "e.4"
                    ));
                    fileBackedTasksManager.subTaskAdd(new SubTask("task_9", "description_9",
                            false, NEW, SUB_TASK, LocalDateTime.parse("23.02.2023_10:00", formatter),
                            Duration.ofMinutes(60), "e.5"
                    ));

                    break;
                case 2:
                   /* System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    System.out.println("taskTitle"); // TODO
                    taskTitle = scanner.next();
                    System.out.println("taskDescription"); // TODO
                    taskDescription = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("parentKey"); // TODO
                        parentKey = scanner.next();
                        System.out.println("taskStatus"); // TODO
                        taskStatus = scanner.next();
                        System.out.println("time <21.02.2023_17:00>"); // TODO
                        startTime = scanner.next();
                        System.out.println("duration"); // TODO
                        duration = scanner.next();
                        fileBackedTasksManager.subTaskUpdate(taskKey, taskTitle, taskDescription, taskStatus, parentKey, startTime, Duration.parse("PT" + duration + "M"));
                    }
                    else if (taskKey.charAt(0) == 't') {
                        System.out.println("taskStatus"); // TODO
                        taskStatus = scanner.next();
                        System.out.println("time <21.02.2023_17:00>"); // TODO
                        startTime = scanner.next();
                        System.out.println("duration"); // TODO
                        duration = scanner.next();
                        fileBackedTasksManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus, startTime, Duration.parse("PT" + duration + "M"));
                    }
                    else if (taskKey.charAt(0) == 'e') {
                        fileBackedTasksManager.epicUpdate(taskKey, taskTitle, taskDescription);
                    }*/
                    break;

                case 3:
                    System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    fileBackedTasksManager.taskRetrieve(taskKey);
                    break;
                case 4:
                    taskManager.collectAllTasks();
                    System.out.println(taskManager.collectAllTasks()); // TODO
                    break;
                case 5:
                    System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    System.out.println(taskManager.collectEpicSubtasks(taskKey)); // TODO
                    taskManager.collectEpicSubtasks(taskKey);
                    break;
                case 6:
                    System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    fileBackedTasksManager.taskDelete(taskKey);
                    break;
                case 7:
                    fileBackedTasksManager.tasksClear();
                    break;
                case 8:
                    System.out.println(inMemoryHistoryManager.getHistory());
                    inMemoryHistoryManager.getHistory();
                    break;
                case 9:
                    InMemoryTaskManager.getPrioritizedTasks();
                    System.out.println(InMemoryTaskManager.getPrioritizedTasks()); // TODO
                    break;
                case 0:
                    return;
            }
        }
    }

/*    public static void getControlOptions() {
        Scanner scanner = new Scanner(System.in);
        int item;
        String taskTitle;
        String taskDescription;
        String taskKey;
        String parentKey;
        String mode;
        String taskStatus;
        String time;
        String duration;
        String startTime;

        while (true) {
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    taskTitle = scanner.next();
                    taskDescription = scanner.next();
                    mode = scanner.next();

                    switch (mode) {
                        case "task":
                            time = scanner.next();
                            duration = scanner.next();
                            fileBackedTasksManager.taskAdd(taskTitle, taskDescription, time,
                                    Duration.parse("PT" + duration + "M")
                            );
                            break;
                        case "epic":
                            fileBackedTasksManager.epicAdd(taskTitle, taskDescription);
                            break;
                        case "subtask":
                            parentKey = scanner.next();
                            time = scanner.next();
                            duration = scanner.next();
                            fileBackedTasksManager.subTaskAdd(taskTitle, taskDescription, parentKey,
                                    time, Duration.parse("PT" + duration + "M")
                            );
                            break;
                    }
                    break;
                case 2:
                    taskKey = scanner.next();
                    taskTitle = scanner.next();
                    taskDescription = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        parentKey = scanner.next();
                        taskStatus = scanner.next();
                        startTime = scanner.next();
                        duration = scanner.next();
                        fileBackedTasksManager.subTaskUpdate(taskKey, taskTitle, taskDescription, taskStatus,
                                parentKey, startTime, Duration.parse("PT" + duration + "M")
                        );
                    }
                    else if (taskKey.charAt(0) == 't') {
                        taskStatus = scanner.next();
                        startTime = scanner.next();
                        duration = scanner.next();
                        fileBackedTasksManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus, startTime,
                                Duration.parse("PT" + duration + "M"));
                    }
                    else if (taskKey.charAt(0) == 'e') {
                        fileBackedTasksManager.epicUpdate(taskKey, taskTitle, taskDescription);
                    }
                    break;
                case 3:
                    taskKey = scanner.next();
                    fileBackedTasksManager.taskRetrieve(taskKey);
                    break;
                case 4:
                    taskManager.collectAllTasks();
                    break;
                case 5:
                    taskKey = scanner.next();
                    taskManager.collectEpicSubtasks(taskKey);
                    break;
                case 6:
                    taskKey = scanner.next();
                    fileBackedTasksManager.taskDelete(taskKey);
                    break;
                case 7:
                    fileBackedTasksManager.tasksClear();
                    break;
                case 8:
                    inMemoryHistoryManager.getHistory();
                    break;
                case 9:
                    InMemoryTaskManager.getPrioritizedTasks();
                    break;
                case 0:
                    return;
            }
        }
    }*/

}
