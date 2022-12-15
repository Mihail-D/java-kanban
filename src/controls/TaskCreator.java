package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.HashMap;
import java.util.Scanner;

public class TaskCreator {
    Scanner scanner = new Scanner(System.in);

    int taskId = 0;
    int epicId = 0;
    int subTaskId = 0;

    public Task taskCreate() {
        System.out.println("title");
        String title = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        String id = getId(1);
        return new Task(title, taskDescription, id, taskStatus);
    }

    public Epic epicCreate() {
        System.out.println("title");
        String title = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        String id = getId(2);
        HashMap<String, String> relatedTasks = new HashMap<>();
        return new Epic(title, taskDescription, id, taskStatus, relatedTasks);
    }

    public SubTask subTaskCreate() {
        System.out.println("parent");
        String parentId = scanner.next();
        Epic parent = ControlManager.epicStorage.get(parentId);

        if (!ControlManager.epicStorage.containsKey(parentId)) {
            System.out.println("!!!");
            parentId = scanner.next();
        }

        System.out.println("title");
        String title = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        String taskId = getId(3);

        parent.relatedSubTask.put(taskId, String.valueOf(taskStatus));
        ControlManager.epicStorage.put(parentId, parent);

        return new SubTask(title, taskDescription, taskId, taskStatus, parentId);
    }

    private String getId(int mode) {

        String id = null;

        switch (mode) {
            case 1:
                taskId++;
                id = "t." + taskId;
                break;
            case 2:
                epicId++;
                id = "e." + epicId;
                break;
            case 3:
                subTaskId++;
                id = "sub." + subTaskId;
                break;
        }

        return id;
    }
}
