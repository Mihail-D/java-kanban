package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;

public class RecordCreator {
    Scanner scanner = new Scanner(System.in);

    int taskId = 0;
    int epicId = 0;
    int subTuskId = 0;

    public Task taskCreate() {
        System.out.println("title");                                    // TODO
        String title = scanner.next();
        System.out.println("description");                               // TODO
        String taskDescription = scanner.next();
        String taskStatus = "NEW";
        String id = getId(1);
        System.out.println(id);                                         // TODO
        return new Task(title, taskDescription, id, taskStatus);
    }

    public Epic epicCreate() {
         System.out.println("title");                                        // TODO
        String title = scanner.next();
        System.out.println("description");                                   // TODO
        String taskDescription = scanner.next();
        String taskStatus = "NEW";
        String id = getId(2);
        HashMap<String, String> relatedTasks = new HashMap<>();               // TODO
        return new Epic(title, taskDescription, id, taskStatus, relatedTasks);
    }

    public SubTask subTaskCreate() {
        System.out.println("parent");                                          // TODO
        String parentId = scanner.next();
        Epic parent = ControlManager.epicStorage.get(parentId);

        System.out.println(parent.toString());
        if (!ControlManager.epicStorage.containsKey(parentId)) {
            System.out.println("!!!");
            parentId = scanner.next();
        }

        System.out.println("title");                                          // TODO
        String title = scanner.next();
        System.out.println("description");                                    // TODO
        String taskDescription = scanner.next();
        String taskStatus = "NEW";
        String subTaskId = getId(3);
        System.out.println(subTaskId);                                         // TODO

        ControlManager.epicStorage.put(parentId, parent);

        return new SubTask (title, taskDescription, parentId, taskStatus, subTaskId);
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
                subTuskId++;
                id = "sub." + subTuskId;
                break;
        }

        return id;
    }
}
