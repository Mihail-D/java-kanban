package controls;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File dataFile;
    private final File historyFile;
    static InMemoryTaskManager taskManager;

    static {
        try {
            taskManager = new InMemoryTaskManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

    public FileBackedTasksManager(File dataFile, File historyFile) throws IOException {
        this.dataFile = dataFile;
        this.historyFile = historyFile;
        restoreTasks(dataFile);
        restoreTasks(historyFile);
    }

    public static InMemoryTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public void taskAdd(Task task) {
        super.taskAdd(task);
        saveTask("newTask");
    }

    @Override
    public void epicAdd(Epic epic) {
        super.epicAdd(epic);
        saveTask("newTask");
    }

    @Override
    public void subTaskAdd(SubTask subTask) {
        super.subTaskAdd(subTask);
        saveTask("newTask");
    }

    @Override
    public String taskRetrieve(String taskKey) {
        super.taskRetrieve(taskKey);
        saveTask("updateTask");

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(
            String taskKey, String taskTitle, String taskDescription,
            String taskStatus, LocalDateTime startTime, Duration duration
    ) {
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
            String taskKey, String taskTitle, String taskDescription, String taskStatus,
            String parentKey, String startTime, Duration duration
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

    void restoreTasks(File file) {
        Task task = null;

        if (file.exists() && !file.isDirectory()) {

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split(",");

                    switch (TaskTypes.valueOf(tokens[5])) {
                        case TASK:
                            task = new Task(tokens[1], tokens[2], Boolean.parseBoolean(tokens[3]),
                                    TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]),
                                    LocalDateTime.parse(tokens[6], DateTimeFormatter.ISO_DATE_TIME), Duration.parse(tokens[7])
                            );
                            task.setTaskId(tokens[0]);

                            break;
                        case EPIC:
                            task = new Epic(tokens[1], tokens[2], Boolean.parseBoolean(tokens[3]),
                                    TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]),
                                    LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
                            );
                            task.setTaskId(tokens[0]);

                            break;
                        case SUB_TASK:
                            task = new SubTask(tokens[1], tokens[2], Boolean.parseBoolean(tokens[3]),
                                    TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]),
                                    LocalDateTime.parse(tokens[7]), Duration.parse(tokens[8]), tokens[6]
                            );
                            task.setTaskId(tokens[0]);

                            Epic parentTask = (Epic) taskManager.getTasksStorage().get(tokens[6]);
                            parentTask.relatedSubTask.put(tokens[0], (SubTask) task);
                            setEpicStatus(tokens[6]);                                                  // TODO
                            setEpicTiming(parentTask);
                            break;
                    }

                    if (file == dataFile) {
                        taskManager.getTasksStorage().put(tokens[0], task);
                    }
                    else {
                        InMemoryHistoryManager.getHistoryStorage().linkLast(task);
                    }
                }

                taskManager.getPrioritizedTasks().addAll(taskManager.getTasksStorage().values());

            } catch (FileNotFoundException e) {
                throw new ManagerLoadException("Не удалось восстановить данные задач");
            } catch (IOException e) {
                throw new RuntimeException("Не удалось восстановить данные задач");
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
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные истории");
        }
    }

    public static int getInitNumber() throws IOException {

        int max = 0;
        File file = new File("./src/data/dataFile.csv");

        if (!file.exists() && !file.isDirectory()) {
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
                file = new File("./src/data/dataFile.csv");
                file.createNewFile();
                return 0;
            }
        }

        return max;
    }
}
