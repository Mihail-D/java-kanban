import controls.*;
import tasks.Task;

import java.util.Scanner;

import static tasks.TaskStages.NEW;

public class Main {

    public static void main(String[] args) {
        getControlOptions();
    }

    public static void getControlOptions() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Scanner scanner = new Scanner(System.in);
        int item;
        String taskTitle;
        String taskDescription;
        String taskKey;
        String parentKey;
        String mode;
        String taskStatus;

        while (true) {
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("taskTitle"); // TODO
                    taskTitle = scanner.next();
                    System.out.println("taskDescription"); // TODO
                    taskDescription = scanner.next();
                    System.out.println("taskMode"); // TODO
                    mode = scanner.next();

                    if (mode.equals("taskMode") || mode.equals("epicMode")) {
                        taskManager.taskAdd(taskTitle, taskDescription, mode);
                    }
                    else if (mode.equals("subTaskMode")) {
                        System.out.println("Parent Key"); // TODO
                        parentKey = scanner.next();
                        taskManager.taskAdd(taskTitle, taskDescription, mode, parentKey);
                    }
                    System.out.println(InMemoryTaskManager.tasksStorage); // TODO
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
                        taskManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus, parentKey);
                    }
                    else if (taskKey.charAt(0) == 't') {
                        System.out.println("taskStatus"); // TODO
                        taskStatus = scanner.next();
                        taskManager.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus);
                    }
                    else if (taskKey.charAt(0) == 'e') {
                        taskManager.taskUpdate(taskKey, taskTitle, taskDescription);
                    }
                    System.out.println(InMemoryTaskManager.tasksStorage); // TODO
                    break;

                case 3:
                    System.out.println("taskKey"); // TODO
                    taskKey = scanner.next();
                    taskManager.taskRetrieve(taskKey);
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

                    if (taskKey.charAt(0) == 's') {
                        parentKey = scanner.next();
                        taskManager.taskDelete(taskKey, parentKey);
                    }
                    else {
                        taskManager.taskDelete(taskKey);
                    }
                    break;
                case 7:
                    taskManager.tasksClear();
                    break;
                case 8:
                   /* if (inMemoryHistoryManager.historyStorage.size() == 0) {
                        inMemoryHistoryManager.historyStorage.addLast(new Task("drg", "gzr", "t.1", true, NEW));
                        inMemoryHistoryManager.historyStorage.addLast(new Task("drg", "gzr", "t.2", true, NEW));
                        inMemoryHistoryManager.historyStorage.addLast(new Task("drg", "gzr", "t.3", true, NEW));
                        inMemoryHistoryManager.historyStorage.addLast(new Task("drg", "gzr", "t.4", true, NEW));
                        inMemoryHistoryManager.historyStorage.addLast(new Task("drg", "gzr", "t.5", true, NEW));
                    }*/

                    inMemoryHistoryManager.getHistory();
                    System.out.println("historyStorage size from Main " + InMemoryHistoryManager.historyStorage.size()); // TODO
                    System.out.println("historyRegister size from Main " + InMemoryHistoryManager.historyRegister); // TODO
                    System.out.println("historyReport from Main " + InMemoryHistoryManager.historyReport); // TODO
                    break;

                case 0:
                    return;
            }
        }
    }
}
