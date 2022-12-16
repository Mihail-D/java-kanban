package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.HashMap;
import java.util.Scanner;

public class ControlManager {
    Scanner scanner = new Scanner(System.in);
    static HashMap<String, Task> tasksStorage = new HashMap<>();
    int taskId = 0;
    int epicId = 0;
    int subTaskId = 0;
    
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
                    taskAdd();
                    System.out.println(tasksStorage); // TODO
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

    public void taskAdd() {
        System.out.println("title");
        String taskTitle = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        System.out.println("task type");
        String mode = scanner.next();
        String taskId = getId(mode);

        switch (mode) {
            case "taskMode":
                tasksStorage.put(taskId, new Task(taskTitle, taskDescription, taskId, taskStatus));
                break;
            case "epicMode":
                //HashMap<String, String> relatedTasks = new HashMap<>();
                tasksStorage.put(taskId, new Epic(taskTitle, taskDescription, taskId, taskStatus, new HashMap<>()));
                break;
            case "subTaskMode":
                System.out.println("parent ID");
                String parentId = scanner.next();
                Epic parentTask = (Epic) tasksStorage.get(parentId);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                tasksStorage.put(taskId, new SubTask(taskTitle, taskDescription, taskId, taskStatus, parentId));
                break;
        }

    }

    private String getId(String taskMode) {
        String id = null;

        switch (taskMode) {
            case "q": // taskMode // TODO   
                taskId++;
                id = "t." + taskId;
                break;
            case "w":  // epicMode // TODO   
                epicId++;
                id = "e." + epicId;
                break;
            case "e": // subTaskMode  // TODO   
                subTaskId++;
                id = "sub." + subTaskId;
                break;
        }

        return id;
    }
}


