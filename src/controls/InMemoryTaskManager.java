package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    Scanner scanner = new Scanner(System.in);
    public static HashMap<String, Task> tasksStorage = new HashMap<>();
    public static List<Task> historyStorage = new ArrayList<>();
    int taskId = 0;

    @Override
    public void taskAdd() {
        System.out.println("title");
        String taskTitle = scanner.next();
        System.out.println("description");
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        System.out.println("task type");
        String mode = scanner.next();
        String taskId = getId(mode);
        boolean isViewed = false;

        switch (mode) {
            case "q": // "taskMode" // TODO
                InMemoryTaskManager.tasksStorage.put(taskId, new Task(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus
                ));
                break;
            case "w": // "epicMode" // TODO
                InMemoryTaskManager.tasksStorage.put(taskId, new Epic(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus, new HashMap<>()
                ));
                break;
            case "e": // "subTaskMode" // TODO
                System.out.println("parent ID");
                String parentId = scanner.next();
                Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(parentId);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                setEpicStatus(parentId);
                InMemoryTaskManager.tasksStorage.put(taskId, new SubTask(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus, parentId
                ));
                break;
        }
        System.out.println(tasksStorage); // TODO
    }

    @Override
    public void taskUpdate(String... args) {
        String taskKey = args[0];
        Task task = tasksStorage.get(taskKey);

        System.out.println("title");
        String title = scanner.next();
        task.setTaskTitle(title);

        System.out.println("description");
        String taskDescription = scanner.next();
        task.setTaskDescription(taskDescription);

        String keyChunk = taskKey.substring(0, 1);

        switch (keyChunk) {
            case "t":
                System.out.println("status");
                String taskStatus = scanner.next();
                task.setTaskStatus(TaskStages.valueOf(taskStatus));
                tasksStorage.put(taskKey, task);
                System.out.println(tasksStorage); // TODO
                break;
            case "e":
                setEpicStatus(taskKey);
                System.out.println(tasksStorage); // TODO
                break;
            case "s":
                String parentKey = args[1];
                Epic parentTask = (Epic) tasksStorage.get(parentKey);

                System.out.println("status");
                taskStatus = scanner.next();
                task.setTaskStatus(TaskStages.valueOf(taskStatus));

                parentTask.relatedSubTask.put(taskKey, taskStatus);

                tasksStorage.put(taskKey, task);
                setEpicStatus(parentKey);
                System.out.println(tasksStorage); // TODO
                break;
        }
    }

    @Override
    public String taskRetrieve(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        String taskTitle = task.getTaskTitle() + ",";
        String taskDescription = task.getTaskDescription() + ",";
        taskKey = task.getTaskId() + ",";
        String taskStatus = String.valueOf(task.getTaskStatus());
        fillHistoryStorage(task);
        inMemoryHistoryManager.add(task);

        return taskTitle + taskDescription + taskKey + taskStatus;
    }

    @Override
    public ArrayList<String>[] collectAllTasks() {
        ArrayList<String> listOfTasks = new ArrayList<>();
        ArrayList<String> listOfEpics = new ArrayList<>();
        ArrayList<String> listOfSubTasks = new ArrayList<>();

        for (String i : tasksStorage.keySet()) {
            if (i.startsWith("t")) {
                listOfTasks.add(taskRetrieve(i));
            }
            else if (i.startsWith("e")) {
                listOfEpics.add(taskRetrieve(i));
            }
            if (i.startsWith("s")) {
                listOfSubTasks.add(taskRetrieve(i));
            }
        }

        return new ArrayList[]{listOfTasks, listOfEpics, listOfSubTasks};
    }

    @Override
    public ArrayList<String> collectEpicSubtasks(String taskKey) {
        ArrayList<String> localTasksList = new ArrayList<>();
        Epic epicTask = (Epic) tasksStorage.get(taskKey);
        HashMap<String, String> relatedSubTasks = epicTask.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            localTasksList.add(taskRetrieve(i));
        }

        return localTasksList;
    }

    @Override
    public void taskDelete(String... args) {
        String taskKey = args[0];
        String keyChunk = taskKey.substring(0, 1);

        switch (keyChunk) {
            case "t":
                tasksStorage.remove(taskKey);
                System.out.println(tasksStorage); // TODO
                break;
            case "e":
                Epic epicTask = (Epic) tasksStorage.get(taskKey);
                HashMap<String, String> relatedSubTasks = epicTask.relatedSubTask;

                for (String i : relatedSubTasks.keySet()) {
                    tasksStorage.remove(i);
                }

                tasksStorage.remove(taskKey);

                System.out.println(tasksStorage); // TODO
                break;
            case "s":
                String parentKey = args[1];
                epicTask = (Epic) tasksStorage.get(parentKey);
                relatedSubTasks = epicTask.relatedSubTask;
                relatedSubTasks.remove(taskKey);
                setEpicStatus(parentKey);
                tasksStorage.remove(taskKey);

                System.out.println(tasksStorage); // TODO
                break;
        }
    }

    @Override
    public void tasksClear() {
        tasksStorage.clear();
    }

    // TODO                                         SERVICE METHODS

    String getId(String taskMode) {
        String id = null;

        switch (taskMode) {
            case "q": // taskMode // TODO
                taskId++;
                id = "t." + taskId;
                break;
            case "w":  // epicMode // TODO
                taskId++;
                id = "e." + taskId;
                break;
            case "e": // subTaskMode  // TODO
                taskId++;
                id = "sub." + taskId;
                break;
        }

        return id;
    }

    public void setEpicStatus(String key) {
        Epic epicTask = (Epic) tasksStorage.get(key);
        TaskStages status = TaskStages.IN_PROGRESS;

        TreeSet<String> set = new TreeSet<>(epicTask.relatedSubTask.values());

        if ((set.size() == 1 && set.contains("NEW")) || set.isEmpty()) {
            status = TaskStages.NEW;
        }
        else if (set.size() == 1 && set.contains("DONE")) {
            status = TaskStages.DONE;
        }

        epicTask.setTaskStatus(status);
    }

    public void fillHistoryStorage(Task task) {
        if (historyStorage.size() == 10) {
            historyStorage.remove(0);
        }
        historyStorage.add(task);
    }
}
