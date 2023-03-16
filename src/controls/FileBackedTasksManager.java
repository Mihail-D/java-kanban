package controls;

import exceptions.ManagerLoadException;
import tasks.TaskStatus;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import server.Constants;
import exceptions.ManagerSaveException;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final List<String> savedTasksList = new ArrayList<>();

    public void tasksForSave() {
        savedTasksList.clear();
        for (Task task : getTaskList()) {
            savedTasksList.add(task.toString());
        }
        for (Epic epicTask : getEpicList()) {
            savedTasksList.add(epicTask.toString());
        }
        for (SubTask subTask : getSubTaskList()) {
            savedTasksList.add(subTask.toString());
        }
    }

    public String historyToString() {
        StringBuilder tasks = new StringBuilder();
        for (Integer taskKey : getHistoryList()) {
            tasks.append(taskKey).append(";");
        }
        return tasks.toString();
    }

    public void loadTasks(ArrayList<String> taskList, String history, String taskKeyCounter) throws IOException {
        String[] tokens = taskKeyCounter.split(":");
        Task.setIdCounter(Integer.parseInt(tokens[1]));
        String[] historySplit = history.split(":");

        taskList.remove(0);
        taskList.remove(taskList.size() - 1);

        List<Integer> keys = new ArrayList<>();

        String[] historyToken = historySplit[1].split(";");

        Arrays.stream(historyToken).forEach(id -> keys.add(Integer.parseInt(id)));

        for (Integer taskKey : keys) {
            for (String task : taskList) {
                String[] taskToken = task.split(";");
                if (taskKey == Integer.parseInt(taskToken[1])) {
                    historyRestore(taskToken);
                }
            }
        }
    }

    private void historyRestore(String[] taskTokens) throws IOException {
        switch (taskTokens[0]) {
            case "Task":
                Task task = new Task(
                        Integer.parseInt(taskTokens[1]),
                        taskTokens[2],
                        taskTokens[3],
                        TaskStatus.valueOf(taskTokens[4]),
                        Instant.parse(taskTokens[5]),
                        Duration.parse(taskTokens[6]),
                        Instant.parse(taskTokens[7])
                );
                addTask(task);
                addToPrioritizedTasks(task);
                break;
            case "SubTask":
                int parentKey = Integer.parseInt(taskTokens[2]);

                SubTask subTask = new SubTask(
                        Integer.parseInt(taskTokens[1]),
                        taskTokens[3],
                        taskTokens[4],
                        TaskStatus.valueOf(taskTokens[5]),
                        Instant.parse(taskTokens[6]),
                        Duration.parse(taskTokens[7]),
                        Instant.parse(taskTokens[8]),
                        parentKey
                );

                addSubTask(subTask, Integer.valueOf(taskTokens[0]));
                addToPrioritizedTasks(subTask);
                break;
            case "Epic":
                Set<SubTask> relatedSubTusks = new LinkedHashSet<>();

                Epic epicTask = new Epic(
                        Integer.parseInt(taskTokens[1]),
                        taskTokens[3],
                        taskTokens[4],
                        TaskStatus.valueOf(taskTokens[5]),
                        Instant.parse(taskTokens[6]),
                        Duration.parse(taskTokens[7]),
                        Instant.parse(taskTokens[8]),
                        relatedSubTusks
                );
                addEpic(epicTask);
                break;
            default:
                System.out.println("Задача не была выгружена из файла; taskKey = " + taskTokens[1]);
                break;
        }
    }

    public void save() {
        try {
            tasksForSave();
            BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.SERVER_URI));
            writer.write("taskKeyCounter:" + Task.taskKeyCounter);
            for (String aTask : savedTasksList) {
                writer.write("\n" + aTask);
            }
            writer.write("\n historyOrder:" + historyToString());
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            throw new ManagerSaveException("Ошибка при сохранении данных" + exception.getMessage());
        }
    }

    public void load() {
        ArrayList<String> tasks = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Constants.SERVER_URI));
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                tasks.add(line);
            }
            reader.close();
            if (!tasks.isEmpty()) {
                loadTasks(tasks, tasks.get(tasks.size() - 1), tasks.get(0));
            }
        } catch (FileNotFoundException e) {
            throw new ManagerLoadException("Не удалось восстановить данные задач");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось восстановить данные задач");
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
    public boolean deleteSubTask(int taskKey) {
        boolean isDeleted = super.deleteSubTask(taskKey);
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
