package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

public class RecordUpdater {
    //ControlManager controlManager = new ControlManager();
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

    public Epic epicUpdate(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        System.out.println(epic);
        System.out.println("title");                                               // TODO
        String title = scanner.next();
        epic.setRecordTitle(title);

        System.out.println("description");                                         // TODO
        String taskDescription = scanner.next();
        epic.setRecordDescription(taskDescription);

        String taskStatus = setEpicStatus(key);
        epic.setRecordStatus(taskStatus);

        return epic;
    }

    public SubTask subTaskUpdate(String subTaskKey, String parentKey) {
        SubTask subTask = ControlManager.subTasksStorage.get(subTaskKey);

        if (!ControlManager.epicStorage.containsKey(parentKey)) {
            System.out.println("Key not found!!!");
            parentKey = scanner.next();
        }

        System.out.println("title");                                               // TODO
        String title = scanner.next();
        subTask.setRecordTitle(title);

        System.out.println("description");                                         // TODO
        String taskDescription = scanner.next();
        subTask.setRecordDescription(taskDescription);

        System.out.println("Введите статус");
        String taskStatus = scanner.next();
        subTask.setRecordStatus(taskStatus);
        setEpicStatus(parentKey);

        return subTask;
    }

    public String setEpicStatus(String key) {
        Epic epicTask = ControlManager.epicStorage.get(key);
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
    }

}
