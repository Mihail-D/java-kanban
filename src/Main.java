import controls.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        getControlOptions();
    }

    public static void getControlOptions() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Scanner scanner = new Scanner(System.in);
        int item;
        String taskKey;
        String parentKey;

        while (true) {
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    taskManager.taskAdd();
                    System.out.println("tasksStorage " + InMemoryTaskManager.tasksStorage); // TODO
                    break;
                case 2:
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("parent key "); // TODO
                        parentKey = scanner.next();
                        taskManager.taskUpdate(taskKey, parentKey);
                    }
                    else {
                        taskManager.taskUpdate(taskKey);
                    }
                    System.out.println("tasksStorage " + InMemoryTaskManager.tasksStorage); // TODO
                    break;
                case 3:
                    System.out.println("task key"); // TODO
                    taskKey = scanner.next();
                    taskManager.taskRetrieve(taskKey);
                    System.out.println("history " + Managers.getDefaultHistory().getHistory()); // TODO
                    break;
                case 4:
                    taskManager.collectAllTasks();
                    System.out.println(taskManager.collectAllTasks()); // TODO
                    break;
                case 5:
                    taskKey = scanner.next();
                    taskManager.collectEpicSubtasks(taskKey);
                    System.out.println(taskManager.collectEpicSubtasks(taskKey)); // TODO
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
                    System.out.println("tasksStorage " + InMemoryTaskManager.tasksStorage); // TODO
                    break;
                case 7:
                    taskManager.tasksClear();
                    System.out.println("tasksStorage " + InMemoryTaskManager.tasksStorage); // TODO
                    break;
                case 8:
                    inMemoryHistoryManager.getHistory();
                    System.out.println("history " + Managers.getDefaultHistory().getHistory()); // TODO
                    break;

                case 0:
                    return;
            }
        }
    }
}
