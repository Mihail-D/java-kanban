package controls;

import exceptions.ManagerLoadException;
import tasks.*;
import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String backedFileName;

    public FileBackedTasksManager() {
        super();
        backedFileName = null;
    }

    public FileBackedTasksManager(String backedFileName) {
        super();
        this.backedFileName = backedFileName;
        if (!isBackedFileExist()) {
            createBackedFile();
        }
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void removeEpic(int taskKey) {
        super.removeEpic(taskKey);
        save();
    }

    @Override
    public void removeTask(int taskKey) {
        super.removeTask(taskKey);
        save();
    }

    @Override
    public void removeSubtask(int taskKey) {
        super.removeSubtask(taskKey);
        save();
    }

    @Override
    public Epic getEpic(int taskKey) {
        Epic epic = super.getEpic(taskKey);
        save();
        return epic;
    }

    @Override
    public Task getTask(int taskKey) {
        Task task = super.getTask(taskKey);
        save();
        return task;
    }

    @Override
    public SubTask getSubtask(int taskKey) {
        SubTask subtask = super.getSubtask(taskKey);
        save();
        return subtask;
    }

    @Override
    public List<Epic> getEpicsCollection() {
        List<Epic> allEpic = super.getEpicsCollection();
        save();
        return allEpic;
    }

    @Override
    public List<SubTask> getSubtasksCollection() {
        List<SubTask> allSubtask = super.getSubtasksCollection();
        save();
        return allSubtask;
    }

    @Override
    public List<Task> getTasksCollection() {
        save();
        return super.getTasksCollection();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backedFileName))) {
            for (Integer taskKey : tasks.keySet()) {
                bufferedWriter.write(tasks.get(taskKey).toString() + "\n");
            }
            for (Integer epicId : epics.keySet()) {
                bufferedWriter.write(epics.get(epicId).toString() + "\n");
            }
            for (Integer subtaskId : subtasks.keySet()) {
                bufferedWriter.write(subtasks.get(subtaskId).toString() + "\n");
            }
            bufferedWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTasksManager load(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getPath());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String[] tokens = bufferedReader.lines().toArray(String[]::new);

            for (int i = 0; i < tokens.length; i++) {
                if (i < tokens.length - 2) {
                    addTaskByType(fileBackedTasksManager, Objects.requireNonNull(taskFromString(tokens[i])));
                }
                else if (i == (tokens.length - 1)) {
                    for (Integer taskKey : historyFromString(tokens[i])) {
                        addTaskToHistoryByType(fileBackedTasksManager, taskKey);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
        return fileBackedTasksManager;
    }

    private int getInitNumber(FileBackedTasksManager fileBackedTasksManager) {
        int maxTaskKey = 0;

        for (Task task : fileBackedTasksManager.getTasksCollection()) {
            if (task.getTaskKey() > maxTaskKey) {
                maxTaskKey = task.getTaskKey();
            }
        }
        return maxTaskKey;
    }

    static private void addTaskByType(TaskManager tasksManager, Task task) {
        if (task.getClass().equals(Epic.class)) {
            tasksManager.updateEpic((Epic) task);
        }
        else if (task.getClass().equals(SubTask.class)) {
            tasksManager.updateSubtask((SubTask) task);
        }
        else {
            tasksManager.updateTask(task);
        }
    }

    static private void addTaskToHistoryByType(FileBackedTasksManager fileBackedTasksManager, Integer taskKey) {
        if (fileBackedTasksManager.getTask(taskKey) != null) {
            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.getTask(taskKey));
        }
        else if (fileBackedTasksManager.getSubtask(taskKey) != null) {
            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.getSubtask(taskKey));
        }
        else if (fileBackedTasksManager.getEpic(taskKey) != null) {
            fileBackedTasksManager.historyManager.add(fileBackedTasksManager.getEpic(taskKey));
        }
    }

    static private Task taskFromString(String value) {
        String[] tokens = value.split(",");
        switch (TaskType.valueOf(tokens[1])) {
            case TASK:
                Task task = new Task(tokens[2], tokens[4], TaskStatus.valueOf(tokens[3]),
                        Duration.parse(tokens[5]), LocalDateTime.parse(tokens[6], Task.getTaskTimeFormatter())
                );
                task.setTaskKey(Integer.parseInt(tokens[0]));
                return task;
            case EPIC:
                Epic epic = new Epic(tokens[2], tokens[4], TaskStatus.valueOf(tokens[3]));
                epic.setTaskKey(Integer.parseInt(tokens[0]));
                return epic;
            case SUB_TASK:
                SubTask subtask = new SubTask(tokens[2], tokens[4], TaskStatus.valueOf(tokens[3]),
                        Duration.parse(tokens[5]), LocalDateTime.parse(tokens[6], Task.getTaskTimeFormatter()),
                        Integer.parseInt(tokens[7])
                );
                subtask.setTaskKey(Integer.parseInt(tokens[0]));
                return subtask;
            default:
                throw new ManagerLoadException("Не удалось создать задачу из строки.");
        }
    }

    private boolean isBackedFileExist() {
        return (Files.exists(Paths.get(backedFileName)));
    }

    private void createBackedFile() {
        Path backedFilePath = Paths.get(backedFileName);
        try {
            Files.createFile(backedFilePath);
        } catch (IOException e) {
            throw new ManagerLoadException("Файл отсутствует.");
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder result = new StringBuilder("\n");

        for (Task task : history) {
            if (result.toString().isBlank()) {
                result.append(task.getTaskKey());
            }
            else {
                result.append(",").append(task.getTaskKey());
            }
        }
        return result.toString();
    }

    static List<Integer> historyFromString(String value) {
        ArrayList<Integer> history = new ArrayList<>();

        if (!value.isBlank()) {
            String[] line = value.split(",");
            for (String taskKey : line) {
                history.add(Integer.parseInt(taskKey));
            }
        }
        return history;
    }

    @Override
    public void load() {

    }
}
