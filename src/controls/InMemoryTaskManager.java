package controls;

import exceptions.ManagerSaveException;
import org.jetbrains.annotations.NotNull;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static tasks.TaskStages.DONE;
import static tasks.TaskStages.NEW;
import static tasks.TaskTypes.*;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    private static HashMap<String, Task> tasksStorage = new LinkedHashMap<>();
    private static Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())
    ));

    public int taskId = FileBackedTasksManager.getInitNumber();
    public static String taskContent;

    public static HashMap<String, Task> getTasksStorage() {
        return tasksStorage;
    }

    @Override
    public void taskAdd(
            String taskTitle, String taskDescription, String startTime, Duration duration
    ) {
        String taskKey = getId(TASK);
        Task task = new Task(taskTitle, taskDescription, taskKey, false, NEW, TASK);
        task.setDuration(duration);
        task.setStartTime(getLocalDateTime(startTime));
        DateRange interval = new DateRange(task.getStartTime(), task.getEndTime(), task.getTaskId());
        advancedTimeOverlappingCheck(interval);

        InMemoryTaskManager.tasksStorage.put(taskKey, task);
        taskContent = getTaskFormattedData(taskKey);
        prioritizedTasks.add(task);
    }

    @Override
    public void epicAdd(String taskTitle, String taskDescription) {
        String taskKey = getId(EPIC);
        Epic epic = new Epic(taskTitle, taskDescription, taskKey, false, NEW, TaskTypes.EPIC, new LinkedHashMap<>());
        InMemoryTaskManager.tasksStorage.put(taskKey, epic);
        taskContent = getTaskFormattedData(taskKey);
        prioritizedTasks.add(epic);
    }

    @Override
    public void subTaskAdd(
            String taskTitle, String taskDescription, String parentKey, String startTime,
            Duration duration
    ) {
        String taskKey = getId(SUB_TASK);
        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(parentKey);
        setEpicStatus(parentKey);
        SubTask task = new SubTask(taskTitle, taskDescription, taskKey, false, NEW, TaskTypes.SUB_TASK, parentKey);
        task.setStartTime(getLocalDateTime(startTime));
        task.setDuration(duration);
        DateRange interval = new DateRange(task.getStartTime(), task.getEndTime(), task.getTaskId());
        advancedTimeOverlappingCheck(interval);

        InMemoryTaskManager.tasksStorage.put(taskKey, task);
        parentTask.relatedSubTask.put(taskKey, task);
        setEpicStatus(parentKey);
        setEpicTiming(parentTask);
        taskContent = getTaskFormattedData(taskKey);
        prioritizedTasks.add(task);
    }

    @Override
    public void taskUpdate(
            String taskKey, String taskTitle, String taskDescription, String taskStatus,
            String startTime, Duration duration
    ) {
        Task task = tasksStorage.get(taskKey);
        task.setTaskTitle(taskTitle);
        task.setTaskDescription(taskDescription);
        TaskTypes taskType = task.getTaskType();
        task.setTaskStatus(TaskStages.valueOf(taskStatus));
        DateRange interval = new DateRange(task.getStartTime(), task.getEndTime(), task.getTaskId());
        advancedTimeOverlappingCheck(interval);

        tasksStorage.put(taskKey, task);
        taskContent = getTaskFormattedData(taskKey);
    }

    @Override
    public void epicUpdate(String taskKey, String taskTitle, String taskDescription) {
        Epic task = (Epic) tasksStorage.get(taskKey);
        task.setTaskTitle(taskTitle);
        task.setTaskDescription(taskDescription);
        TaskTypes taskType = task.getTaskType();

        setEpicStatus(taskKey);
        setEpicTiming(task);

        taskContent = getTaskFormattedData(taskKey);
    }

    @Override
    public void subTaskUpdate(
            String taskKey, String taskTitle, String taskDescription, String taskStatus,
            String parentKey, String startTime, Duration duration
    ) {
        SubTask task = (SubTask) tasksStorage.get(taskKey);
        task.setTaskTitle(taskTitle);
        task.setTaskDescription(taskDescription);
        TaskTypes taskType = task.getTaskType();
        Epic parentTask = (Epic) tasksStorage.get(parentKey);
        setEpicTiming(parentTask);
        task.setTaskStatus(TaskStages.valueOf(taskStatus));
        DateRange interval = new DateRange(task.getStartTime(), task.getEndTime(), task.getTaskId());
        advancedTimeOverlappingCheck(interval);

        parentTask.relatedSubTask.put(taskKey, task);
        tasksStorage.put(taskKey, task);
        setEpicStatus(parentKey);
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
    public void taskDelete(String taskKey) {
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
                SubTask subTask = (SubTask) tasksStorage.get(taskKey);
                String parentKey = subTask.getParentId();
                epicTask = (Epic) tasksStorage.get(parentKey);
                relatedSubTasks = epicTask.relatedSubTask;
                relatedSubTasks.remove(taskKey);
                setEpicStatus(parentKey);
                setEpicTiming(epicTask);
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
            if (tasksStorage.get(i).getTaskType() == TASK) {
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

    public static Set<Task> getPrioritizedTasks() {             // TODO
        for (Task i : prioritizedTasks) {
            System.out.println(i.getTaskId() + " " + i.getStartTime());
        }

        return prioritizedTasks;
    }

    public String getId(@NotNull TaskTypes taskType) {
        String id = null;

        switch (taskType) {
            case TASK:
                taskId++;
                id = "t." + taskId;
                break;
            case EPIC:
                taskId++;
                id = "e." + taskId;
                break;
            case SUB_TASK:
                taskId++;
                id = "s." + taskId;
                break;
        }

        return id;
    }

    public void setEpicStatus(String taskKey) {
        Epic epicTask = (Epic) tasksStorage.get(taskKey);
        TaskStages status = TaskStages.IN_PROGRESS;

        Set<SubTask> set = new HashSet<>(epicTask.relatedSubTask.values());

        if (set.isEmpty()) {
            status = TaskStages.NEW;
            epicTask.setTaskStatus(status);
            return;
        }

        boolean isAllNew = set.stream().allMatch(i -> i.getTaskStatus() == NEW);
        boolean isAllDone = set.stream().allMatch(i -> i.getTaskStatus() == DONE);

        for (SubTask i : set) {
            if (isAllNew || set.isEmpty()) {
                status = TaskStages.NEW;
                break;
            }
            else if (isAllDone) {
                status = TaskStages.DONE;
                break;
            }
        }

        epicTask.setTaskStatus(status);
    }

    public void setEpicTiming(Epic epicTask) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.ZERO;

        for (SubTask i : epicTask.relatedSubTask.values()) {
            if (i.getStartTime().isBefore(startTime)) {
                startTime = i.getStartTime();
            }
        }

        for (SubTask i : epicTask.relatedSubTask.values()) {
            duration = i.getDuration().plus(duration);
        }

        epicTask.setStartTime(startTime.truncatedTo(ChronoUnit.MINUTES));
        epicTask.setDuration(duration);
        epicTask.getEndTime();
    }

    public String getTaskFormattedData(String taskKey) {
        Task task = tasksStorage.get(taskKey);
        String taskTitle = task.getTaskTitle() + ",";
        String taskDescription = task.getTaskDescription() + ",";
        taskKey = task.getTaskId() + ",";
        String isViewed = task.isViewed() + ",";
        String taskStatus = task.getTaskStatus() + ",";
        String taskType = String.valueOf(task.getTaskType());
        LocalDateTime time = task.getStartTime();
        Duration duration = task.getDuration();
        String result;

        if (task.getClass() == SubTask.class) {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus + taskType + ","
                    + ((SubTask) task).getParentId() + "," + time + "," + duration + "," + task.getEndTime();
        }
        else if (task.getClass() == Epic.class && time == null) {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus + taskType;
        }
        else {
            result = taskKey + taskTitle + taskDescription + isViewed + taskStatus + taskType + ","
                    + time + "," + duration + "," + task.getEndTime();
        }

        return result;
    }

    public LocalDateTime getLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");
        return LocalDateTime.parse(time, formatter);
    }

    public void advancedTimeOverlappingCheck(DateRange interval) {
        List<DateRange> ranges = new ArrayList<>();

        for (Task i : prioritizedTasks) {
            if (i.getStartTime() != null) {
                ranges.add(new DateRange(i.getStartTime(), i.getEndTime(), i.getTaskId()));
            }
        }

        for (DateRange i : ranges) {
            if (i.getTaskKey().equals(interval.taskKey)) {
                continue;
            }

            if (!(i.isOverlappingAtStart (interval.start, interval.stop)
                    || i.isOverlappingAtStop(interval.start, interval.stop))) {
                throw new ManagerSaveException("Время новой задачи пересекается с ранее созданной");
            }
        }
    }
}