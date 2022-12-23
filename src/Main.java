import controls.InMemoryHistoryManager;
import controls.InMemoryTaskManager;

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
                    break;
                case 2:
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        parentKey = scanner.next();
                        taskManager.taskUpdate(taskKey, parentKey);
                    }
                    else {
                        taskManager.taskUpdate(taskKey);
                    }
                    break;
                case 3:
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
                    inMemoryHistoryManager.getHistory();
                    break;

                case 0:
                    return;
            }
        }
    }
}
