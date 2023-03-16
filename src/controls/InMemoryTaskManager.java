package controls;

import exceptions.ManagerTaskTimeOverlappingException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.*;

import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;

public class InMemoryTaskManager implements TaskManager {

    public HistoryManager historyManager = Managers.getDefaultHistory();
    private final HashMap<Integer, Task> tasksStorage = new HashMap<>();
    private final HashMap<Integer, Epic> epicsStorage = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasksStorage = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getTaskStartTime));

    @Override
    public HashMap<Integer, Task> getTasksMap() {
        return tasksStorage;
    }

    @Override
    public HashMap<Integer, Epic> getEpicsMap() {
        return epicsStorage;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksMap() {
        return subTasksStorage;
    }

    @Override
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTasksStorage.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicsStorage.values());
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasksStorage.values());
    }

    @Override
    public List<Task> collectAllTasks() {
        List<Task> tasksCollection = new ArrayList<>();
        tasksCollection.addAll(getEpicList());
        tasksCollection.addAll(getSubTaskList());
        tasksCollection.addAll(getTaskList());
        return tasksCollection;
    }

    @Override
    public List<String> getAllTasksStringList() {
        List<String> tasksCollection = new ArrayList<>();
        collectAllTasks().forEach(task -> tasksCollection.add(task.toString()));
        return tasksCollection;
    }

    @Override
    public Task getTask(Integer taskKey) {
        return tasksStorage.get(taskKey);
    }

    @Override
    public Epic getEpic(Integer taskKey) {
        return epicsStorage.get(taskKey);
    }

    @Override
    public SubTask getSubTask(Integer taskKey) {
        return subTasksStorage.get(taskKey);
    }

    public ArrayList<Integer> getHistoryList() {
        ArrayList<Integer> historyList = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            historyList.add(task.getTaskKey());
        }
        return historyList;
    }

    public ArrayList<String> getHistoryStringList() {
        ArrayList<String> historyList = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            historyList.add(task.getTaskKey().toString());
        }
        return historyList;
    }

    @Override
    public boolean deleteTask(Integer taskKey) throws IOException {
        if (!tasksStorage.isEmpty() && tasksStorage.get(taskKey) != null) {
            prioritizedTasks.remove(tasksStorage.get(taskKey));
            tasksStorage.remove(taskKey);
            historyManager.removeHistory(taskKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(Integer taskKey) throws IOException {
        boolean isDeleted = false;
        if (!epicsStorage.isEmpty() && epicsStorage.get(taskKey) != null) {
            epicsStorage.remove(taskKey);
            historyManager.removeHistory(taskKey);
            isDeleted = true;
        }
        if (isDeleted) {
            isDeleted = clearRelatedSubTusks(taskKey);
        }
        return isDeleted;
    }

    @Override
    public boolean deleteSubTask(int taskKey) {
        SubTask subTask = subTasksStorage.get(taskKey);

        if (!subTasksStorage.isEmpty() && subTasksStorage.get(subTask.getTaskKey()) != null) {
            Epic epic = epicsStorage.get(subTask.getParentKey());
            epic.removeChildSubTask(subTask);
            prioritizedTasks.remove(subTask);
            subTasksStorage.remove(subTask.getTaskKey());
            historyManager.removeHistory(subTask.getTaskKey());
            return true;
        }
        return false;
    }

    @Override
    public boolean clearRelatedSubTusks(Integer taskKey) throws IOException {
        if (!subTasksStorage.isEmpty()) {
            for (SubTask subTask : subTasksStorage.values()) {
                if (subTask.getParentKey() == taskKey) {
                    prioritizedTasks.remove(subTask);
                    subTasksStorage.remove(subTask.getTaskKey());
                    historyManager.removeHistory(subTask.getTaskKey());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateEpic(Epic epicTask, Integer taskKey) throws IOException {
        historyManager.add(epicTask, 0);
        setEpicStatus(taskKey);
    }

    @Override
    public void updateSubTask(SubTask subTask, Integer taskKey) throws IOException {
        historyManager.add(subTask, 0);
    }

    @Override
    public void updateTask(Task task) throws IOException {
        historyManager.add(task, 0);
    }

    @Override
    public void addEpic(Epic epicTask) throws IOException {
        if (advancedTimeOverlappingCheck(epicTask)) {
            int key;
            if (epicTask.getTaskKey() != null) {
                key = epicTask.getTaskKey();
            }
            else {
                key = epicTask.setTaskKey();
            }
            epicsStorage.put(key, epicTask);
            historyManager.add(epicTask, 0);
        }
        else {
            throw new ManagerTaskTimeOverlappingException(
                    "Обнаружено пересечение " + epicTask.getTaskKey() + "времени " + epicTask.getTaskStartTime()
            );
        }
    }

    @Override
    public void addSubTask(SubTask subTask, Integer epicTaskKey) throws IOException {
        if (advancedTimeOverlappingCheck(subTask)) {
            int key;
            if (subTask.getTaskKey() != null) {
                key = subTask.getTaskKey();
            }
            else {
                key = subTask.setTaskKey();
            }
            epicsStorage.get(epicTaskKey).addChildSubTask(subTask);
            subTasksStorage.put(key, subTask);
            subTasksStorage.get(key).setRelatedSubtasks(epicTaskKey);
            historyManager.add(subTask, 0);
            prioritizedTasks.add(subTask);
        }
        else {
            throw new ManagerTaskTimeOverlappingException(
                    "Обнаружено пересечение " + subTask.getTaskKey() + "времени " + subTask.getTaskStartTime()
            );
        }
    }

    @Override
    public void addTask(Task task) throws IOException {
        if (advancedTimeOverlappingCheck(task)) {
            int key;
            if (task.getTaskKey() != null) {
                key = task.getTaskKey();
            }
            else {
                key = task.setTaskKey();
            }
            tasksStorage.put(key, task);
            historyManager.add(task, 0);
            prioritizedTasks.add(task);
        }
        else {
            throw new ManagerTaskTimeOverlappingException(
                    "Обнаружено пересечение " + task.getTaskKey() + "времени " + task.getTaskStartTime()
            );
        }
    }

    @Override
    public void tasksClear() {
        tasksStorage.clear();
        subTasksStorage.clear();
        epicsStorage.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void setEpicStatus(Integer taskKey) {
        Epic epicTask = epicsStorage.get(taskKey);
        TaskStatus status = TaskStatus.IN_PROGRESS;

        Set<SubTask> set = epicTask.getRelatedSubTasks();

        if (set.isEmpty()) {
            status = NEW;
            epicTask.setTaskStatus(status);
            return;
        }

        boolean isAllNew = set.stream().allMatch(i -> i.getTaskStatus() == NEW);
        boolean isAllDone = set.stream().allMatch(i -> i.getTaskStatus() == DONE);

        for (SubTask i : set) {
            if (isAllNew || set.isEmpty()) {
                status = NEW;
                break;
            }
            else if (isAllDone) {
                status = DONE;
                break;
            }
        }

        epicTask.setTaskStatus(status);
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    public boolean advancedTimeOverlappingCheck(Task task) {
        if (task instanceof Epic) {
            return true;
        }
        for (Task value : getPrioritizedTasks()) {
            if (value.getTaskKey().equals(task.getTaskKey())) {
                continue;
            }
            if (task.getTaskEndTime() != null) {
                if ((task.getTaskStartTime().compareTo(value.getTaskStartTime()) <=
                        0 && task.getTaskEndTime().compareTo(value.getTaskStartTime()) >= 0)
                        || (value.getTaskStartTime().compareTo(task.getTaskStartTime())
                        <= 0 && value.getTaskEndTime().compareTo(task.getTaskStartTime()) >= 0)) {
                    return false;
                }
            }
            if (task.getTaskEndTime() == null) {
                if ((task.getTaskStartTime().compareTo(value.getTaskStartTime()) <= 0)
                        ||
                        (value.getTaskStartTime().compareTo(task.getTaskStartTime())
                                <= 0 && value.getTaskEndTime().compareTo(task.getTaskStartTime()) >= 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    public TreeSet<Task> getPrioritizedTasksSet() {
        return prioritizedTasks;
    }

    public List<Task> getPrioritizedTasks() {
        if (!prioritizedTasks.isEmpty()) {
            return new ArrayList<>(prioritizedTasks);
        }
        else {
            return new ArrayList<>();
        }
    }
}