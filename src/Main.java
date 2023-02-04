import controls.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    static InMemoryTaskManager taskManager;
    static InMemoryHistoryManager inMemoryHistoryManager;
    static FileBackedTasksManager fileBackedTasksManager;

    public static void main(String[] args) throws IOException {
        String PATH = "./src/data";
        Files.createDirectories(Paths.get(PATH));
        boolean dataFile = new File(PATH + File.separator + "dataFile.csv").createNewFile();
        boolean historyFile = new File(PATH + File.separator + "historyFile.csv").createNewFile();

        fileBackedTasksManager = new FileBackedTasksManager(dataFile, historyFile);
        taskManager = new InMemoryTaskManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();

        getControlOptions();
    }

    public static void getControlOptions() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int item;
        String taskTitle;
        String taskDescription;
        String taskKey;
        String parentKey;
        String mode;
        String taskStatus;

        while (true) {
            System.out.println("MENU"); // TODO
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("taskTitle"); // TODO
                    taskTitle = scanner.next();
                    System.out.println("taskDescription"); // TODO
                    taskDescription = scanner.next();
                    System.out.println("mode taskMode epicMode subTaskMode"); // TODO
                    mode = scanner.next();

                    if (mode.equals("taskMode") || mode.equals("epicMode")) {
                        fileBackedTasksManager.taskAdd(taskTitle, taskDescription, mode);
                    }
                    else if (mode.equals("subTaskMode")) {
                        System.out.println("parentKey"); // TODO

                        parentKey = scanner.next();
                        fileBackedTasksManager.taskAdd(taskTitle, taskDescription, mode, parentKey);
                    }

                    break;
                case 2:
                    System.out.println("taskKey"); // TODO
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
                        fileBackedTasksManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus, parentKey);
                    }
                    else if (taskKey.charAt(0) == 't') {
                        System.out.println("taskStatus"); // TODO
                        taskStatus = scanner.next();
                        fileBackedTasksManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus);
                    }
                    else if (taskKey.charAt(0) == 'e') {
                        fileBackedTasksManager.taskUpdate(taskKey, taskTitle, taskDescription);
                    }
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

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("parentKey"); // TODO
                        parentKey = scanner.next();

                        fileBackedTasksManager.taskDelete(taskKey, parentKey);
                    }
                    else {
                        fileBackedTasksManager.taskDelete(taskKey);
                    }
                    break;

                case 7:
                    fileBackedTasksManager.tasksClear();
                    break;
                case 8:
                    System.out.println(inMemoryHistoryManager.getHistory());
                    inMemoryHistoryManager.getHistory();
                    break;

                case 0:
                    return;
            }
        }
    }
}
