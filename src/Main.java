import controls.*;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        getControlOptions();
    }

    public static void getControlOptions() throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        
        fileBackedTasksManager.getInitNumber();
        fileBackedTasksManager.restoreTasks();
        fileBackedTasksManager.restoreHistory();

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
                        fileBackedTasksManager.taskEdit(taskKey, taskTitle, taskDescription, taskStatus, parentKey);
                    }
                    else if (taskKey.charAt(0) == 't') {
                        System.out.println("taskStatus"); // TODO
                        taskStatus = scanner.next();
                        fileBackedTasksManager.taskEdit(taskKey, taskTitle, taskDescription, taskStatus);
                    }
                    else if (taskKey.charAt(0) == 'e') {
                        fileBackedTasksManager.taskEdit(taskKey, taskTitle, taskDescription);
                    }
                    break;

                case 3:
                    System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    fileBackedTasksManager.taskGet(taskKey);
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

                        fileBackedTasksManager.dataDelete(taskKey, parentKey);
                    }
                    else {
                        fileBackedTasksManager.dataDelete(taskKey);
                    }
                    break;

                case 7:
                    fileBackedTasksManager.dataClear();
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
