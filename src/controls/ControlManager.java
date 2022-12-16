package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class ControlManager {
    Scanner scanner = new Scanner(System.in);
    static HashMap<String, Task> tasksStorage = new HashMap<>();
    int taskId = 0;
    int epicId = 0;
    int subTaskId = 0;

    public void getControlOptions() {

        Scanner scanner = new Scanner(System.in);
        int item;
        String taskKey;
        String subTaskKey;

        while (true) {
            System.out.println("Тип действий с записями");
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("Создание объекта.");
                    taskAdd();
                    System.out.println(tasksStorage); // TODO
                    break;
                case 2:
                    System.out.println("Обновление объекта.");

                    System.out.println("Task Key");
                    taskKey = scanner.next();

                    if (taskKey.charAt(0) == 's') {
                        System.out.println("Parent Key");
                        String parentKey = scanner.next();
                        taskUpdate(taskKey, parentKey);
                    }
                    else {
                        taskUpdate(taskKey);
                    }
                    break;
/*                case 3:
                    System.out.println("Получение по идентификатору.");
                    System.out.println("Какой тип записи получить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Получение данных Задачи.");
                            System.out.println("Введите ключ");
                            taskKey = scanner.next();

                            break;
                        case 2:
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();

                            break;
                        case 3:
                            System.out.println("Получение данных Подзадачи.");
                            System.out.println("Введите ключ");
                            subTaskKey = scanner.next();

                            break;
                    }
                    break;*/
/*                case 4:
                    System.out.println("Получение списка всех задач.");

                    break;*/
/*                case 5:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
                    System.out.println("Введите ключ");
                    String key = scanner.next();

                    break;*/
/*                case 6:
                    System.out.println("Удаление по идентификатору.");
                    System.out.println("Введите ключ подзадачи");
                    taskKey = scanner.next();
                    System.out.println("Введите ключ эпика");
                    String parentKey = scanner.next();

                    break;*/
/*                case 7:
                    System.out.println("Удаление всех задач.");

                    break;*/
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
            case "q": // "taskMode" // TODO
                tasksStorage.put(taskId, new Task(taskTitle, taskDescription, taskId, taskStatus));
                break;
            case "w": // "epicMode" // TODO
                tasksStorage.put(taskId, new Epic(taskTitle, taskDescription, taskId, taskStatus, new HashMap<>()));
                break;
            case "e": // "subTaskMode" // TODO
                System.out.println("parent ID");
                String parentId = scanner.next();
                Epic parentTask = (Epic) tasksStorage.get(parentId);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                setEpicStatus(parentId);
                tasksStorage.put(taskId, new SubTask(taskTitle, taskDescription, taskId, taskStatus, parentId));
                break;
        }
    }

    public void taskUpdate(String... args) {
        String taskKey = args[0];
        Task task = tasksStorage.get(taskKey);

        System.out.println("title");
        String title = scanner.next();
        task.setTaskTitle(title);

        System.out.println("description");
        String taskDescription = scanner.next();
        task.setTaskDescription(taskDescription);

        String keyChunk = taskKey.substring(0, 1);

        switch (keyChunk) {
            case "t":
                System.out.println("status");
                String taskStatus = scanner.next();
                task.setTaskStatus(taskStatus);
                tasksStorage.put(taskKey, task);
                System.out.println(tasksStorage); // TODO
                break;
            case "e":
                setEpicStatus(taskKey);
                System.out.println(tasksStorage); // TODO
                break;
            case "s":
                String parentKey = args[1];
                Epic parentTask = (Epic) tasksStorage.get(parentKey);

                System.out.println("status");
                taskStatus = scanner.next();
                task.setTaskStatus(taskStatus);

                parentTask.relatedSubTask.put(taskKey, taskStatus);

                tasksStorage.put(taskKey, task);
                setEpicStatus(parentKey);
                System.out.println(tasksStorage); // TODO
                break;
        }

    }

    // TODO                                         SERVICE METHODS

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

    public void setEpicStatus(String key) {
        Epic epicTask = (Epic) tasksStorage.get(key);
        TaskStages status = TaskStages.IN_PROGRESS;

        TreeSet<String> set = new TreeSet<>(epicTask.relatedSubTask.values());

        if ((set.size() == 1 && set.contains("NEW")) || set.isEmpty()) {
            status = TaskStages.NEW;
        }
        else if (set.size() == 1 && set.contains("DONE")) {
            status = TaskStages.DONE;
        }

        epicTask.setTaskStatus(String.valueOf(status));
    }
}


