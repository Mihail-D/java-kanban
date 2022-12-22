package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class InMemoryHistoryManager implements HistoryManager{
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    Scanner scanner = new Scanner(System.in);

    @Override
    public void taskAdd() {
        System.out.println("title");
        String taskTitle = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        System.out.println("task type");
        String mode = scanner.next();
        String taskId = taskManager.getId(mode);

        switch (mode) {
            case "q": // "taskMode" // TODO
                InMemoryTaskManager.tasksStorage.put(taskId, new Task(taskTitle, taskDescription, taskId, taskStatus));
                break;
            case "w": // "epicMode" // TODO
                InMemoryTaskManager.tasksStorage.put(taskId, new Epic(taskTitle, taskDescription, taskId, taskStatus, new HashMap<>()));
                break;
            case "e": // "subTaskMode" // TODO
                System.out.println("parent ID");
                String parentId = scanner.next();
                Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(parentId);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                taskManager.setEpicStatus(parentId);
                InMemoryTaskManager.tasksStorage.put(taskId, new SubTask(taskTitle, taskDescription, taskId, taskStatus, parentId));
                break;
        }
    }

    @Override
    public List<Task> getHistory() {
        return InMemoryTaskManager.historyStorage;
    }
}
