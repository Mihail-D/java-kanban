import controls.ControlManager;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        getControlOptions();
    }

    public static void getControlOptions() {
        ControlManager controlManager = new ControlManager();
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
                    controlManager.taskAdd();
                    System.out.println(ControlManager.tasksStorage); // TODO
                    break;
                case 2:
                    System.out.println("Обновление объекта.");

                    System.out.println("Task Key");
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("Parent Key");
                        parentKey = scanner.next();
                        controlManager.taskUpdate(taskKey, parentKey);
                    }
                    else {
                        controlManager.taskUpdate(taskKey);
                    }
                    break;
                case 3:
                    System.out.println("Получение по идентификатору.");
                    System.out.println("Task Key");
                    taskKey = scanner.next();
                    controlManager.taskRetrieve(taskKey);

                    System.out.println(controlManager.taskRetrieve(taskKey));               // TODO

                    break;
                case 4:
                    System.out.println("Получение списка всех задач.");
                    controlManager.collectAllTasks();
                    System.out.println(Arrays.toString(controlManager.collectAllTasks())); // TODO
                    break;
                case 5:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
                    System.out.println("Введите ключ");
                    taskKey = scanner.next();
                    controlManager.collectEpicSubtasks(taskKey);

                    System.out.println(controlManager.collectEpicSubtasks(taskKey));        // TODO

                    break;
                case 6:
                    System.out.println("Удаление по идентификатору.");
                    System.out.println("Введите ключ задачи");
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("Parent Key");
                        parentKey = scanner.next();
                        controlManager.taskDelete(taskKey, parentKey);
                    }
                    else {
                        controlManager.taskDelete(taskKey);
                    }
                    break;
                case 7:
                    System.out.println(ControlManager.tasksStorage);               // TODO
                    System.out.println("Удаление всех задач.");
                    controlManager.tasksClear();

                    System.out.println(ControlManager.tasksStorage);                // TODO

                    break;
                case 0:
                    return;
            }
        }
    }

}
