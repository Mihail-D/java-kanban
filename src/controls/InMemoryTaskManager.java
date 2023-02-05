package controls;

import org.jetbrains.annotations.NotNull;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public static HashMap<String, Task> tasksStorage = new HashMap<>();
    public static String PATH = "./src/data";
    int taskId = getInitNumber();
    public static String taskContent;

    @Override
    public void taskAdd(String @NotNull ... args) { // taskTitle, taskDescription, mode, parentKey
        TaskStages taskStatus = TaskStages.NEW;
        String taskId = getId(args[2]);
        boolean isViewed = false;


        switch (args[2]) {
            case "taskMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Task(args[0], args[1], taskId,
                        isViewed, taskStatus
                ));
                break;
            case "epicMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Epic(args[0], args[1], taskId,
                        isViewed, taskStatus, new HashMap<>()
                ));
                break;
            case "subTaskMode":
                Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(args[3]);
                parentTask.relatedSubTask.put(taskId, String.valueOf(taskStatus));
                setEpicStatus(args[3]);
                InMemoryTaskManager.tasksStorage.put(taskId, new SubTask(args[0], args[1], taskId,
                        isViewed, taskStatus, args[3]
                ));
                break;
        }
        taskContent = getTaskFormattedData(taskId);
    }

    @Override
    public void taskUpdate(String @NotNull ... args) {
        String taskKey = args[0];
        Task task = tasksStorage.get(taskKey);

        task.setTaskTitle(args[1]);

        task.setTaskDescription(args[2]);

        String keyChunk = taskKey.substring(0, 1);

        switch (keyChunk) {
            case "t":
                task.setTaskStatus(TaskStages.valueOf(args[3]));
                tasksStorage.put(taskKey, task);
                break;
            case "e":
                setEpicStatus(taskKey);
                break;
            case "s":
                String parentKey = args[4];
                Epic parentTask = (Epic) tasksStorage.get(parentKey);

                task.setTaskStatus(TaskStages.valueOf(args[3]));

                parentTask.relatedSubTask.put(taskKey, args[3]);

                tasksStorage.put(taskKey, task);
                setEpicStatus(parentKey);
                break;
        }
    }

    @Override
    public String taskRetrieve(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        getTaskFormattedData(taskKey);
        inMemoryHistoryManager.addHistory(task);
        task.setViewed();
        taskContent = getTaskFormattedData(taskKey);

        return taskContent;
    }

    @Override
    public void taskDelete(String @NotNull ... args) {
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
                    inMemoryHistoryManager.removeHistoryRecord(i);
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
        inMemoryHistoryManager.removeHistoryRecord(taskKey);
    }

    @Override
    public void tasksClear() {
        tasksStorage.clear();
        inMemoryHistoryManager.clearHistoryStorage();
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


    public String getId(@NotNull String taskMode) {
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
                id = "s." + taskId;
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
        String isViewed = task.isViewed() + ",";
        String taskStatus = String.valueOf(task.getTaskStatus());
        String result;
        if (task.getClass() == SubTask.class) {
            result =
                    taskKey + taskTitle + taskDescription + isViewed + taskStatus
                            + "," + ((SubTask) task).getParentId();
        }
        else {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus;
        }

        return result;
    }

    public int getInitNumber() {
        File file = new File(PATH + File.separator + "dataFile.csv");
        int max = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] arr = line.split(",");
                int number = Integer.parseInt(arr[0].substring(2));
                if (number > max) {
                    max = number;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return max;
    }
}
