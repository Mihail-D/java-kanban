package controls;

import org.jetbrains.annotations.NotNull;
import tasks.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static tasks.TaskStages.DONE;
import static tasks.TaskStages.NEW;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public static HashMap<String, Task> tasksStorage = new LinkedHashMap<>();
    public File file;
    int taskId = getInitNumber();
    public static String taskContent;

    @Override
    public void separateTaskAdd(
            String taskTitle, String taskDescription, String mode, String startTime, Duration duration
    ) {
        String taskId = getId(mode);

        switch (mode) {
            case "taskMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Task(taskTitle, taskDescription,
                        taskId, false, NEW, TaskTypes.TASK,
                        getLocalDateTime(startTime), duration
                ));
                break;
            case "epicMode":
                InMemoryTaskManager.tasksStorage.put(taskId, new Epic(taskTitle, taskDescription,
                        taskId, false, NEW, TaskTypes.EPIC, new LinkedHashMap<>(),
                        getLocalDateTime(startTime), duration
                ));
                break;
        }
        taskContent = getTaskFormattedData(taskId);
    }

    @Override
    public void subTaskAdd(
            String taskTitle, String taskDescription, String parentKey, String startTime, Duration duration
    ) {
        String taskId = getId("subTaskMode");
        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(parentKey);
        setEpicStatus(parentKey);
        SubTask subTask = new SubTask(taskTitle, taskDescription,
                taskId, false, NEW, TaskTypes.SUB_TASK,
                parentKey, getLocalDateTime(startTime), duration
        );

        InMemoryTaskManager.tasksStorage.put(taskId, subTask);

        parentTask.relatedSubTask.put(taskId, subTask);
        taskContent = getTaskFormattedData(taskId);
    }

    @Override
    public void taskUpdate(String @NotNull ... args) {
        String taskKey = args[0];
        Task task = tasksStorage.get(taskKey);

        task.setTaskTitle(args[1]);

        task.setTaskDescription(args[2]);

        TaskTypes taskType = task.getTaskType();

        switch (taskType) {
            case TASK:
                task.setTaskStatus(TaskStages.valueOf(args[3]));
                tasksStorage.put(taskKey, task);
                break;
            case EPIC:
                setEpicStatus(taskKey);
                break;
            case SUB_TASK:
                String parentKey = args[4];
                Epic parentTask = (Epic) tasksStorage.get(parentKey);

                task.setTaskStatus(TaskStages.valueOf(args[3]));

                parentTask.relatedSubTask.put(taskKey, (SubTask) task);

                tasksStorage.put(taskKey, task);
                setEpicStatus(parentKey);
                break;
        }
        taskContent = getTaskFormattedData(taskKey);
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
        Task task = tasksStorage.get(taskKey);

        switch (task.getTaskType()) {
            case TASK:
                tasksStorage.remove(taskKey);
                break;
            case EPIC:
                Epic epicTask = (Epic) tasksStorage.get(taskKey);
                LinkedHashMap<String, SubTask> relatedSubTasks = epicTask.relatedSubTask;

                for (String i : relatedSubTasks.keySet()) {
                    tasksStorage.remove(i);
                    inMemoryHistoryManager.removeHistoryRecord(i);
                }

                tasksStorage.remove(taskKey);
                break;
            case SUB_TASK:
                String parentKey = args[1];
                epicTask = (Epic) tasksStorage.get(parentKey);
                relatedSubTasks = epicTask.relatedSubTask;
                relatedSubTasks.remove(taskKey);
                setEpicStatus(parentKey);
                tasksStorage.remove(taskKey);
                break;
        }
        inMemoryHistoryManager.removeHistoryRecord(taskKey);
        taskContent = null;
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
            if (tasksStorage.get(i).getTaskType() == TaskTypes.TASK) {
                listOfTasks.add(getTaskFormattedData(i));
            }
            else if (tasksStorage.get(i).getTaskType() == TaskTypes.EPIC) {
                listOfEpics.add(getTaskFormattedData(i));
            }
            if (tasksStorage.get(i).getTaskType() == TaskTypes.SUB_TASK) {
                listOfSubTasks.add(getTaskFormattedData(i));
            }
        }

        return Stream.of(listOfTasks, listOfEpics, listOfSubTasks).collect(toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<String> collectEpicSubtasks(String taskKey) {
        ArrayList<String> localTasksList = new ArrayList<>();
        Epic epicTask = (Epic) tasksStorage.get(taskKey);
        LinkedHashMap<String, SubTask> relatedSubTasks = epicTask.relatedSubTask;

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

        Set<SubTask> set = new HashSet<>(epicTask.relatedSubTask.values());

        for (SubTask i : set) {
            if ((set.size() == 1 && i.getTaskStatus() == NEW) || set.isEmpty()) {
                status = TaskStages.NEW;
            }
            else if (set.size() == 1 && i.getTaskStatus() == DONE) {
                status = TaskStages.DONE;
            }
            else if ( set.stream().allMatch(e -> e.getTaskStatus() == NEW )){
                status = TaskStages.NEW;
                break;
            }
            else if ( set.stream().allMatch(j -> j.getTaskStatus() == DONE )){
                status = TaskStages.DONE;
                break;
            }
        }

        epicTask.setTaskStatus(status);
    }

    public String getTaskFormattedData(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        String taskTitle = task.getTaskTitle() + ",";
        String taskDescription = task.getTaskDescription() + ",";
        taskKey = task.getTaskId() + ",";
        String isViewed = task.isViewed() + ",";
        String taskStatus = task.getTaskStatus() + ",";
        String taskType = task.getTaskType() + ",";
        LocalDateTime time = task.getStartTime();
        Duration duration = task.getDuration();
        String result;
        if (task.getClass() == SubTask.class) {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus + taskType
                    + ((SubTask) task).getParentId() + "," + time
                    + "," + duration + "," + task.getEndTime();
        }
        else {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus
                    + taskType + time + "," + duration + "," + task.getEndTime();
        }

        return result;
    }

    public int getInitNumber() {
        file = new File(FileBackedTasksManager.PATH + File.separator + "dataFile.csv");
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

    public LocalDateTime getLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        return LocalDateTime.parse(time, formatter);
    }
}
