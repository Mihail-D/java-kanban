import controls.InMemoryHistoryManager;
import controls.InMemoryTaskManager;

import java.util.Arrays;
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
            System.out.println("Тип действий с записями");
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("Создание объекта.");
                    taskManager.taskAdd();
                    System.out.println(InMemoryTaskManager.tasksStorage); // TODO
                    break;
                case 2:
                    System.out.println("Обновление объекта.");

                    System.out.println("Task Key");
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("Parent Key");
                        parentKey = scanner.next();
                        taskManager.taskUpdate(taskKey, parentKey);
                    }
                    else {
                        taskManager.taskUpdate(taskKey);
                    }
                    break;
                case 3:
                    System.out.println("Получение по идентификатору.");
                    System.out.println("Task Key");
                    taskKey = scanner.next();
                    taskManager.taskRetrieve(taskKey);

                    System.out.println(taskManager.taskRetrieve(taskKey));               // TODO

                    break;
                case 4:
                    System.out.println("Получение списка всех задач.");
                    taskManager.collectAllTasks();
                    System.out.println(Arrays.toString(taskManager.collectAllTasks())); // TODO
                    break;
                case 5:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
                    System.out.println("Введите ключ");
                    taskKey = scanner.next();
                    taskManager.collectEpicSubtasks(taskKey);

                    System.out.println(taskManager.collectEpicSubtasks(taskKey));        // TODO

                    break;
                case 6:
                    System.out.println("Удаление по идентификатору.");
                    System.out.println("Введите ключ задачи");
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("Parent Key");
                        parentKey = scanner.next();
                        taskManager.taskDelete(taskKey, parentKey);
                    }
                    else {
                        taskManager.taskDelete(taskKey);
                    }
                    break;
                case 7:
                    System.out.println(InMemoryTaskManager.tasksStorage);               // TODO
                    System.out.println("Удаление всех задач.");
                    taskManager.tasksClear();

                    System.out.println(InMemoryTaskManager.tasksStorage);                // TODO

                    break;
                case 8:
                    System.out.println("10 последних задач.");
                    inMemoryHistoryManager.getHistory();
                    System.out.println(inMemoryHistoryManager.getHistory()); // TODO
                    break;

                case 0:
                    return;
            }
        }
    }

}
