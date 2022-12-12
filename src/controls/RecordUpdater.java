package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.Scanner;
import java.util.TreeSet;

public class RecordUpdater {
    Scanner scanner = new Scanner(System.in);

    public Task taskUpdate(String key) {
        Task task = ControlManager.tasksStorage.get(key);
        System.out.println(task); // TODO

        System.out.println("title");
        String title = scanner.next();
        task.setRecordTitle(title);

        System.out.println("description");
        String taskDescription = scanner.next();
        task.setRecordDescription(taskDescription);

        System.out.println("status");
        String taskStatus = scanner.next();
        task.setRecordStatus(taskStatus);

        return task;
    }

    public Epic epicUpdate(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        System.out.println(epic);                                                 // TODO
        System.out.println("title");                                               // TODO
        String title = scanner.next();
        epic.setRecordTitle(title);

        System.out.println("description");                                         // TODO
        String epicDescription = scanner.next();
        epic.setRecordDescription(epicDescription);

        return epic;
    }

    public SubTask subTaskUpdate(String subTaskKey, String parentKey) {
        SubTask subTask = ControlManager.subTasksStorage.get(subTaskKey);
        Epic parentTask = ControlManager.epicStorage.get(parentKey);

        System.out.println("title");                                               // TODO
        String title = scanner.next();
        subTask.setRecordTitle(title);

        System.out.println("description");                                         // TODO
        String taskDescription = scanner.next();
        subTask.setRecordDescription(taskDescription);

        System.out.println("Введите статус");
        String taskStatus = scanner.next();

        subTask.setRecordStatus(taskStatus);

        parentTask.relatedSubTask.put(subTaskKey, taskStatus);

        return subTask;
    }

    public Epic setEpicStatus(String key) {
        Epic epicTask = ControlManager.epicStorage.get(key);
        String status = "IN_PROGRESS";
        TreeSet<String> set = new TreeSet<>(epicTask.relatedSubTask.values());

        System.out.println(set);                                                    // TODO

        if (set.size() == 1 && set.contains("NEW")) {
            status = "NEW";
        }
        else if (set.size() == 1 && set.contains("DONE")) {
            status = "DONE";
        }

        epicTask.setRecordStatus(status);
        return epicTask;
    }
}
