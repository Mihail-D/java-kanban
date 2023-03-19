package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subtasks;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getTaskStartTime));
    protected HistoryManager historyManager;
    protected int initTaskKey;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.initTaskKey = 0;
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setTaskKey(getNextTaskKey());
        epics.put(epic.getTaskKey(), epic);
    }

    @Override
    public void addTask(Task task) {
        if (advancedTimeOverlappingCheck(task)) {
            task.setTaskKey(getNextTaskKey());
            addToPrioritizedTasks(task);
            tasks.put(task.getTaskKey(), task);
        }
    }

    @Override
    public void addSubtask(SubTask subtask) {
        if (advancedTimeOverlappingCheck(subtask)) {
            Epic parentTask = epics.get(subtask.getParentKey());
            subtask.setTaskKey(getNextTaskKey());
            subtasks.put(subtask.getTaskKey(), subtask);
            addToPrioritizedTasks(subtask);
            parentTask.addChild(subtask.getTaskKey());
            updateEpicStatus(subtask.getParentKey());
            updateEpicTimeInterval(parentTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getTaskKey(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (advancedTimeOverlappingCheck(task)) {
            updatePrioritizedTasks(task);
            tasks.put(task.getTaskKey(), task);
        }
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        if (advancedTimeOverlappingCheck(subtask)) {
            Epic parentTask = epics.get(subtask.getParentKey());
            prioritizedTasks.add(subtask);
            updatePrioritizedTasks(subtask);
            subtasks.put(subtask.getTaskKey(), subtask);
            updateEpicStatus(subtask.getParentKey());
            updateEpicTimeInterval(parentTask);
        }
    }

    @Override
    public void removeEpic(int taskKey) {
        for (Integer subTaskKey : epics.get(taskKey).getRelatedSubTasks()) {
            historyManager.remove(subTaskKey);
            prioritizedTasksDeleteValue(subtasks.get(subTaskKey));
            subtasks.remove(subTaskKey);
        }
        historyManager.remove(taskKey);
        epics.remove(taskKey);
    }

    @Override
    public void removeTask(int taskKey) {
        prioritizedTasksDeleteValue(tasks.get(taskKey));
        historyManager.remove(taskKey);
        tasks.remove(taskKey);
    }

    @Override
    public void removeSubtask(int taskKey) {
        SubTask subtask = subtasks.get(taskKey);
        Epic parentTask = epics.get(subtask.getParentKey());
        prioritizedTasks.remove(subtask);
        historyManager.remove(taskKey);
        epics.get(subtasks.get(taskKey).getParentKey()).removeSubtask(taskKey);
        updateEpicStatus(subtasks.get(taskKey).getParentKey());
        updateEpicTimeInterval(parentTask);
        prioritizedTasksDeleteValue(subtasks.get(taskKey));
        subtasks.remove(taskKey);
    }

    @Override
    public Epic getEpic(int taskKey) {

        historyManager.add(epics.get(taskKey));
        return epics.get(taskKey);

    }

    @Override
    public Task getTask(int taskKey) {

        historyManager.add(tasks.get(taskKey));
        return tasks.get(taskKey);

    }

    @Override
    public SubTask getSubtask(int taskKey) {

        historyManager.add(subtasks.get(taskKey));
        return subtasks.get(taskKey);

    }

    @Override
    public List<Epic> getEpicsCollection() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubtasksCollection() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getTasksCollection() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getEpicRelatedSubtasks(int epicTaskKey) {
        ArrayList<SubTask> epicRelatedSubtasks = new ArrayList<>();
        for (int subtaskId : epics.get(epicTaskKey).getRelatedSubTasks()) {
            epicRelatedSubtasks.add(subtasks.get(subtaskId));
        }
        return epicRelatedSubtasks;
    }

    @Override
    public void clearEpics() {
        List<Integer> epicTaskKeys = new ArrayList<>(epics.keySet());
        for (int taskKey : epicTaskKeys) {
            removeEpic(taskKey);
        }
    }

    @Override
    public void clearTasks() {
        List<Integer> taskTaskKeys = new ArrayList<>(tasks.keySet());
        for (int taskKey : taskTaskKeys) {
            removeTask(taskKey);
        }
    }

    @Override
    public void clearSubTasks() {
        List<Integer> subTaskKeys = new ArrayList<>(subtasks.keySet());
        for (int taskKey : subTaskKeys) {
            removeSubtask(taskKey);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicTimeInterval(Epic epicTask) {
        LocalDateTime startTime = epicTask.getTaskStartTime();
        java.time.Duration duration = java.time.Duration.ZERO;
        LocalDateTime endTime;

        List<SubTask> epicTasks = new ArrayList<>();

        for (SubTask i : subtasks.values()) {
            if (i.getParentKey() == epicTask.getTaskKey()) {
                epicTasks.add(i);
            }
        }

        for (SubTask i : epicTasks) {
            if (epicTask.getRelatedSubTasks().size() == 1) {
                startTime = i.getTaskStartTime();
                break;
            }
            else if (i.getTaskStartTime().isBefore(startTime)) {
                startTime = i.getTaskStartTime();
            }
        }

        for (SubTask i : epicTasks) {
            duration = i.getTaskDuration().plus(duration);
        }

        epicTask.setTaskStartTime(startTime.truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        epicTask.setTaskDuration(duration);
        epicTask.getTaskEndTime();
    }

    private void updateEpicStatus(int taskKey) {

        Epic epicTask = epics.get(taskKey);
        TaskStatus status = TaskStatus.IN_PROGRESS;

        ArrayList<Integer> set = epicTask.getRelatedSubTasks();

        if (set.isEmpty()) {
            status = NEW;
            epicTask.setTaskStatus(status);
            return;
        }

        boolean isAllNew = set.stream().allMatch(i -> subtasks.get(i).getTaskStatus() == NEW);
        boolean isAllDone = set.stream().allMatch(i -> subtasks.get(i).getTaskStatus() == DONE);

        if (isAllNew || set.isEmpty()) {
            status = NEW;
        }
        else if (isAllDone) {
            status = DONE;
        }

        epicTask.setTaskStatus(status);
    }

    private boolean advancedTimeOverlappingCheck(Task task) {
        if (task instanceof Epic) {
            return true;
        }
        for (Task value : getPrioritizedTasks()) {
            if (value.getTaskKey() == (task.getTaskKey())) {
                continue;
            }
            if (task.getTaskEndTime() != null) {
                if ((!task.getTaskStartTime().isAfter(value.getTaskStartTime())
                        && !task.getTaskEndTime().isBefore(value.getTaskStartTime()))
                        || (!value.getTaskStartTime().isAfter(task.getTaskStartTime())
                        && !value.getTaskEndTime().isBefore(task.getTaskStartTime()))) {
                    return false;
                }
            }
            if (task.getTaskEndTime() == null) {
                if ((!task.getTaskStartTime().isAfter(value.getTaskStartTime()))
                        ||
                        (!value.getTaskStartTime().isAfter(task.getTaskStartTime())
                                && !value.getTaskEndTime().isBefore(task.getTaskStartTime()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    private void updatePrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    private void prioritizedTasksDeleteValue(Task task) {
        prioritizedTasks.remove(task);
    }

    private int getNextTaskKey() {
        this.initTaskKey++;
        return initTaskKey;
    }

    protected void setLastTaskId(int value) {
        this.initTaskKey = value;
    }

    @Override
    public void load() {

    }

}