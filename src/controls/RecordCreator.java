package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.Scanner;

public class RecordCreator {
    ControlManager controlManager = new ControlManager();
    Scanner scanner = new Scanner(System.in);

    int taskId = 0;
    int epicId = 0;
    int subTuskId = 0;

    public Task taskCreate() {
        System.out.println("title"); // TODO TODO TODO
        String title = scanner.next();
        System.out.println("description");// TODO TODO TODO
        String taskDescription = scanner.next();
        System.out.println("status");// TODO TODO TODO
        String taskStatus = scanner.next();
        String id = getId(1);
        System.out.println(id);// TODO TODO TODO
        return new Task(title, taskDescription, id, taskStatus);
    }

    public Epic epicCreate() {
        System.out.println("title"); // TODO TODO TODO
        String title = scanner.next();
        System.out.println("description");// TODO TODO TODO
        String taskDescription = scanner.next();
        System.out.println("status");// TODO TODO TODO
        String taskStatus = scanner.next();
        String id = getId(2);
        System.out.println(id);// TODO TODO TODO
        return new Epic(title, taskDescription, id, taskStatus);
    }

    public SubTask subTaskCreate() {
        System.out.println("parent"); // TODO TODO TODO
        String parentId = scanner.next();
        //controlManager = new ControlManager();

        if (!controlManager.epicStorage.containsKey(parentId)) {

        }

        System.out.println("title"); // TODO TODO TODO
        String title = scanner.next();
        System.out.println("description");// TODO TODO TODO
        String taskDescription = scanner.next();
        System.out.println("status");// TODO TODO TODO
        String taskStatus = scanner.next();
        String subTaskId = getId(3);
        System.out.println(subTaskId);// TODO TODO TODO
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
