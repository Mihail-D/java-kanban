package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public static HashMap<String, Task> tasksStorage = new HashMap<>();
    Scanner scanner = new Scanner(System.in);
    int taskId = 0;

    @Override
    public void taskAdd() {
        String taskTitle = scanner.next();
        String taskDescription = scanner.next();
        TaskStages taskStatus = TaskStages.NEW;
        String mode = scanner.next();
        String taskId = getId(mode);
        boolean isViewed = false;

        switch (mode) {
            case "taskMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Task(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus
                ));
                break;
            case "epicMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Epic(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus, new HashMap<>()
                ));
                break;
            case "subTaskMode":
                String parentId = scanner.next();
                Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(parentId);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                setEpicStatus(parentId);
                InMemoryTaskManager.tasksStorage.put(taskId, new SubTask(taskTitle, taskDescription, taskId,
                        isViewed, taskStatus, parentId
                ));
                break;
        }
    }

    @Override
    public void taskUpdate(String... args) {
        String taskKey = args[0];
        Task task = tasksStorage.get(taskKey);

        String title = scanner.next();
        task.setTaskTitle(title);

        String taskDescription = scanner.next();
        task.setTaskDescription(taskDescription);

        String keyChunk = taskKey.substring(0, 1);

        switch (keyChunk) {
            case "t":
                String taskStatus = scanner.next();
                task.setTaskStatus(TaskStages.valueOf(taskStatus));
                tasksStorage.put(taskKey, task);
                break;
            case "e":
                setEpicStatus(taskKey);
                break;
            case "s":
                String parentKey = args[1];
                Epic parentTask = (Epic) tasksStorage.get(parentKey);

                taskStatus = scanner.next();
                task.setTaskStatus(TaskStages.valueOf(taskStatus));

                parentTask.relatedSubTask.put(taskKey, taskStatus);

                tasksStorage.put(taskKey, task);
                setEpicStatus(parentKey);
                break;
        }
    }

    @Override
    public String taskRetrieve(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        getTaskFormattedData(taskKey);
        inMemoryHistoryManager.add(task);

        return getTaskFormattedData(taskKey);
    }

    @Override
    public ArrayList<ArrayList<String>> collectAllTasks() {
        ArrayList<String> listOfTasks = new ArrayList<>();
        ArrayList<String> listOfEpics = new ArrayList<>();
        ArrayList<String> listOfSubTasks = new ArrayList<>();

        for (String i : tasksStorage.keySet()) {
            if (i.startsWith("t")) {
                listOfTasks.add(getTaskFormattedData(i));
            }
            else if (i.startsWith("e")) {
                listOfEpics.add(getTaskFormattedData(i));
            }
            if (i.startsWith("s")) {
                listOfSubTasks.add(getTaskFormattedData(i));
            }
        }

        return Stream.of(listOfTasks, listOfEpics, listOfSubTasks).collect(toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<String> collectEpicSubtasks(String taskKey) {
        ArrayList<String> localTasksList = new ArrayList<>();
        Epic epicTask = (Epic) tasksStorage.get(taskKey);
        HashMap<String, String> relatedSubTasks = epicTask.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            localTasksList.add(getTaskFormattedData(i));
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
                break;
            case "e":
                Epic epicTask = (Epic) tasksStorage.get(taskKey);
                HashMap<String, String> relatedSubTasks = epicTask.relatedSubTask;

                for (String i : relatedSubTasks.keySet()) {
                    tasksStorage.remove(i);
                }

                tasksStorage.remove(taskKey);

                break;
            case "s":
                String parentKey = args[1];
                epicTask = (Epic) tasksStorage.get(parentKey);
                relatedSubTasks = epicTask.relatedSubTask;
                relatedSubTasks.remove(taskKey);
                setEpicStatus(parentKey);
                tasksStorage.remove(taskKey);
                break;
        }
    }

    @Override
    public void tasksClear() {
        tasksStorage.clear();
    }

    String getId(String taskMode) {
        String id = null;

        switch (taskMode) {
            case "taskMode":
                taskId++;
                id = "t." + taskId;
                break;
            case "epicMode":
                taskId++;
                id = "e." + taskId;
                break;
            case "subTaskMode":
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

    public String getTaskFormattedData(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        String taskTitle = task.getTaskTitle() + ",";
        String taskDescription = task.getTaskDescription() + ",";
        taskKey = task.getTaskId() + ",";
        String taskStatus = String.valueOf(task.getTaskStatus());

        return taskTitle + taskDescription + taskKey + taskStatus;
    }
}
