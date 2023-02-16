package controls;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tasks.TaskTypes.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static final String PATH = "./src/data";
    private final File dataFile;
    private final File historyFile;

    public FileBackedTasksManager(File dataFile, File historyFile) {
        this.dataFile = dataFile;
        this.historyFile = historyFile;
        restoreTasks(dataFile);
        restoreTasks(historyFile);
    }

    @Override
    public void taskAdd(String taskTitle, String taskDescription, String time, Duration duration) {
        super.taskAdd(taskTitle, taskDescription, time, duration);
        saveTask("newTask");
    }

    @Override
    public void epicAdd(String taskTitle, String taskDescription) {
        super.epicAdd(taskTitle, taskDescription);
        saveTask("newTask");
    }

    @Override
    public void subTaskAdd(String taskTitle, String taskDescription, String parentKey, String time, Duration duration) {
        super.subTaskAdd(taskTitle, taskDescription, parentKey, time, duration);
        saveTask("newTask");
    }

    @Override
    public String taskRetrieve(String taskKey) {
        super.taskRetrieve(taskKey);
        saveTask("updateTask");

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(String taskKey, String taskTitle, String taskDescription, String taskStatus, String startTime, Duration duration) {
        super.taskUpdate(taskKey, taskTitle, taskDescription, taskStatus, startTime, duration);
        saveTask("updateTask");
    }

    @Override
    public void epicUpdate(String taskKey, String taskTitle, String taskDescription) {
        super.epicUpdate(taskKey, taskTitle, taskDescription);
        saveTask("updateTask");
    }

    @Override
    public void subTaskUpdate(
            String taskKey, String taskTitle, String taskDescription, String taskStatus, String parentKey, String startTime, Duration duration
    ) {
        super.subTaskUpdate(taskKey, taskTitle, taskDescription, taskStatus, parentKey, startTime, duration);
        saveTask("updateTask");
    }

    @Override
    public void taskDelete(String taskKey) {
        super.taskDelete(taskKey);
        saveTask("updateTask");
    }

    @Override
    public void tasksClear() {
        super.tasksClear();
        saveTask("deleteTask");
    }

    private void restoreTasks(File file) {
        Task task = null;

        if (file.exists() && !file.isDirectory()) {

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split(",");

                    if (TaskTypes.valueOf(tokens[5]) == TASK) {
                        task = new Task(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]), TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]));
                        task.setStartTime(LocalDateTime.parse(tokens[6]));
                        task.setDuration(Duration.parse(tokens[7]));
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == EPIC) {                                         // TODO
                        task = new Epic(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]), TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), new LinkedHashMap<>());
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == SUB_TASK) {
                        task = new SubTask(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]), TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), tokens[6]);
                        task.setStartTime(LocalDateTime.parse(tokens[7]));
                        task.setDuration(Duration.parse(tokens[8]));

                        Epic parentTask = (Epic) InMemoryTaskManager.getTasksStorage().get(tokens[6]);
                        parentTask.relatedSubTask.put(tokens[0], (SubTask) task);
                        setEpicStatus(tokens[6]);

                        // TODO
                        setEpicTiming(parentTask);
                        //parentTask.setStartTime(LocalDateTime.parse(tokens[6]));
                        //parentTask.setDuration(Duration.parse(tokens[7]));

                    }

                    if (file == dataFile) {
                        InMemoryTaskManager.getTasksStorage().put(tokens[0], task);
                    }
                    else {
                        InMemoryHistoryManager.getHistoryStorage().linkLast(task);
                    }
                }

                InMemoryTaskManager.getPrioritizedTasks().addAll(InMemoryTaskManager.getTasksStorage().values()); // TODO

            } catch (ManagerLoadException | FileNotFoundException e) {
                System.out.println("Не удалось восстановить данные задач");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveTask(String saveMode) {

        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(dataFile, UTF_8)))
        ) {

            for (String entry : getTasksStorage().keySet()) {
                writer.append(getTaskFormattedData(getTasksStorage().get(entry).getTaskId()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные задач");
        }

        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(historyFile, UTF_8)))
        ) {
            Map<String, Node> list = InMemoryHistoryManager.getHistoryRegister();

            for (String i : list.keySet()) {
                String newData = getTaskFormattedData(list.get(i).getTask().getTaskId());

                if (!newData.equals(taskContent)) {
                    writer.append(newData);
                    writer.newLine();
                }

            }
            if (taskContent != null && !(saveMode.equals("newTask") || saveMode.equals("deleteTask"))) {
                writer.append(taskContent);
            }

        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные истории");
        }
    }
}
