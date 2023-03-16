package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import server.KVServer;
import server.KVTaskClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static server.Constants.*;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient taskClient;

    public HttpTaskManager() {
        super();
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        try {
            taskClient = new KVTaskClient(SERVER_URI);
        } catch (InterruptedException | IOException exception) {
            System.out.println(exception.getMessage());
        }

    }

    @Override
    public void save() {
        try {
            taskClient.put(TASK_KEY, String.join("\n", getAllTasksStringList()));
            taskClient.put(HISTORY_KEY, String.join("\n", getHistoryStringList()));
            taskClient.put(KEY, String.valueOf(Task.taskKeyCounter));
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("При сохранении произошла ошибка: " + exception.getMessage());
        }
    }

    @Override
    public void load() {
        ArrayList<String> tasksList = new ArrayList<>();
        try {
            String[] line = taskClient.load(TASK_KEY).split("\n");
            String historyLine = taskClient.load(HISTORY_KEY);
            String taskKeyCounter = taskClient.load(KEY);
            tasksList.addAll(Arrays.asList(line));
            if (line.length > 0 && historyLine.length() > 0 && taskKeyCounter.length() > 0) {
                loadTasks(tasksList, historyLine, taskKeyCounter);
            }
        } catch (InterruptedException | IOException exception) {
            throw new ManagerLoadException("Ошибка при загрузке: " + exception.getMessage());
        }
    }

    @Override
    public void addTask(Task task) throws IOException {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, Integer epicTaskKey) throws IOException {
        super.addSubTask(subTask, epicTaskKey);
        save();
    }

    @Override
    public void addEpic(Epic epicTask) throws IOException {
        super.addEpic(epicTask);
        save();
    }

    @Override
    public void updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, Integer taskKey) throws IOException {
        super.updateSubTask(subTask, taskKey);
        save();
    }

    @Override
    public void updateEpic(Epic epicTask, Integer taskKey) throws IOException {
        super.updateEpic(epicTask, taskKey);
        save();
    }

    @Override
    public boolean deleteTask(Integer taskKey) throws IOException {
        boolean isDeleted = super.deleteTask(taskKey);
        save();
        return isDeleted;
    }

    @Override
    public boolean deleteEpic(Integer taskKey) throws IOException {
        boolean isDeleted = super.deleteEpic(taskKey);
        save();
        return isDeleted;
    }

    @Override
    public boolean deleteSubTask(int taskKey) {
        boolean isDeleted = super.deleteSubTask(taskKey);
        save();
        return isDeleted;
    }

    @Override
    public boolean clearRelatedSubTusks(Integer taskKey) throws IOException {
        boolean isClear = super.clearRelatedSubTusks(taskKey);
        save();
        return isClear;
    }

    @Override
    public void tasksClear() {
        super.tasksClear();
        save();
    }
}
