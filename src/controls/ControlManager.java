package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;
import java.util.Scanner;

public class ControlManager {

    static HashMap<String, Task> tasksStorage = new HashMap<>();

    public void getControlOptions() {

        Scanner scanner = new Scanner(System.in);
        int item;

        while (true) {
            System.out.println("Тип действий с записями");
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("Создание объекта. Сам объект должен передаваться в качестве параметра.");
                    System.out.println("Выбрать типа объекта");
                    item = scanner.nextInt();
                    switch (item) {
                        case 1:

                            break;
                        case 2:

                            break;
                        case 3:

                            break;
                    }
                    break;
                case 2:
                    System.out.println("Обновление.");
                    System.out.println("Какой тип записи обновить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Введите номер ключа");
                            String taskKey = scanner.next();

                            break;
                        case 2:
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();

                            break;
                        case 3:
                            System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            System.out.println("Введите ключ основной задачи");
                            String parentKey = scanner.next();

                            break;
                    }
                    break;
                case 3:
                    System.out.println("Получение по идентификатору.");
                    System.out.println("Какой тип записи получить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Получение данных Задачи.");
                            System.out.println("Введите ключ");
                            String taskKey = scanner.next();

                            break;
                        case 2:
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();

                            break;
                        case 3:
                            System.out.println("Получение данных Подзадачи.");
                            System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();

                            break;
                    }
                    break;
                case 4:
                    System.out.println("Получение списка всех задач.");

                    break;
                case 5:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
                    System.out.println("Введите ключ");
                    String key = scanner.next();

                    break;
                case 6:
                    System.out.println("Удаление по идентификатору.");
                    System.out.println("Введите ключ подзадачи");
                    String taskKey = scanner.next();
                    System.out.println("Введите ключ эпика");
                    String parentKey = scanner.next();

                    break;
                case 7:
                    System.out.println("Удаление всех задач.");

                    break;
                case 0:
                    return;
            }
        }
    }
}


