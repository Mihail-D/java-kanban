package controls;
import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;

public class RecordUpdater {
    ControlManager controlManager = new ControlManager();
    RecordCreator recordCreator = new RecordCreator();
    Scanner scanner = new Scanner(System.in);

    public Task taskUpdate(String key) {
        System.out.println("title");                                               // TODO
        String title = scanner.next();
        System.out.println("description");                                         // TODO
        String taskDescription = scanner.next();
        System.out.println("Введите статус");
        String taskStatus = scanner.next();
        return new Task(title, taskDescription, key, taskStatus);
    }


}
