package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class RecordCreator {
    ControlManager controlManager = new ControlManager();
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
        Epic parent = controlManager.epicStorage.get(parentId);
        //System.out.println(parent.toString());
        //if (!controlManager.epicStorage.containsKey(parentId)) {}

        System.out.println("title");                                          // TODO
        String title = scanner.next();
        System.out.println("description");                                    // TODO
        String taskDescription = scanner.next();
        String taskStatus = "NEW";
        String subTaskId = getId(3);
        System.out.println(subTaskId);                                         // TODO

        controlManager.epicStorage.put(parentId, parent);

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

/*    public String getEpicStatus(String key) {
        Epic epicTask = controlManager.epicStorage.get(key);
        String status = "IN_PROGRESS";
        HashMap<String, String> relatedSubtasks = epicTask.relatedSubTask;
        TreeSet<String> set = new TreeSet<>(relatedSubtasks.values());

        if (set.size() == 1 && set.contains("NEW")) {
            status = "NEW";
        }
        else if (set.size() == 1 && set.contains("DONE")) {
            status = "DONE";
        }

        return status;
    }*/
}
