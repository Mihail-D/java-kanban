package controls;

import records.Epic;
import records.Task;

import java.util.Scanner;

public class RecordCreator {
    //ControlManager controlManager = new ControlManager();
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


  /*public Epic epicTaskCreate() {

  };*/
  /*public SubTask subTaskCreate() {

  };*/

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
