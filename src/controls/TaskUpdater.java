package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.Scanner;
import java.util.TreeSet;

public class TaskUpdater {
    Scanner scanner = new Scanner(System.in);

    public Task taskUpdate(String key) {
        Task task = ControlManager.tasksStorage.get(key);

        //System.out.println("title");
        String title = scanner.next();
        task.setTaskTitle(title);

        //System.out.println("description");
        String taskDescription = scanner.next();
        task.setTaskDescription(taskDescription);

        //System.out.println("status");
        String taskStatus = scanner.next();
        task.setTaskStatus(taskStatus);

        return task;
    }

    public Epic epicUpdate(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        //System.out.println("title");
        String title = scanner.next();
        epic.setTaskTitle(title);

        //System.out.println("description");
        String epicDescription = scanner.next();
        epic.setTaskDescription(epicDescription);

        return epic;
    }

    public SubTask subTaskUpdate(String subTaskKey, String parentKey) {
        SubTask subTask = ControlManager.subTasksStorage.get(subTaskKey);
        Epic parentTask = ControlManager.epicStorage.get(parentKey);

        //System.out.println("title");
        String title = scanner.next();
        subTask.setTaskTitle(title);

        //System.out.println("description");
        String taskDescription = scanner.next();
        subTask.setTaskDescription(taskDescription);

        //System.out.println("Введите статус");
        String taskStatus = scanner.next();
        subTask.setTaskStatus(taskStatus);
        parentTask.relatedSubTask.put(subTaskKey, taskStatus);

        return subTask;
    }

    public Epic setEpicStatus(String key) {
        Epic epicTask = ControlManager.epicStorage.get(key);
        String status = "IN_PROGRESS";
        TreeSet<String> set = new TreeSet<>(epicTask.relatedSubTask.values());

        if ((set.size() == 1 && set.contains("NEW")) || set.isEmpty()) {
            status = "NEW";
        }
        else if (set.size() == 1 && set.contains("DONE")) {
            status = "DONE";
        }

        epicTask.setTaskStatus(status);
        return epicTask;
    }
}
