package controls;

import org.jetbrains.annotations.NotNull;
import tasks.*;

import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tasks.TaskTypes.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static final String PATH = "./src/data";

    private File dataFile;
    private File historyFile;

    public FileBackedTasksManager(File dataFile, File historyFile) {
        this.dataFile = dataFile;
        this.historyFile = historyFile;
        restoreTasks();
        restoreHistory();
    }

    @Override
    public void taskAdd(String @NotNull ... args) {
        super.taskAdd(args);
        saveTask("newTask");
    }

    @Override
    public String taskRetrieve(String taskKey) {
        super.taskRetrieve(taskKey);
        saveTask("updateTask");

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(String @NotNull ... args) {
        super.taskUpdate(args);
        saveTask("updateTask");
    }

    @Override
    public void taskDelete(String @NotNull ... args) {
        super.taskDelete(args);
        saveTask("updateTask");
    }

    @Override
    public void tasksClear() {
        super.tasksClear();
        saveTask("deleteTask");
    }

    private void restoreTasks() {
        file = dataFile;
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
                        task = new Task(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5])
                        );
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == EPIC) {
                        task = new Epic(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), new HashMap<>()
                        );
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == SUB_TASK) {
                        task = new SubTask(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), tokens[6]
                        );
                        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[6]);
                        parentTask.relatedSubTask.put(tokens[0], String.valueOf(TaskStages.valueOf(tokens[4])));
                        setEpicStatus(tokens[6]);
                    }

                    InMemoryTaskManager.tasksStorage.put(tokens[0], task);
                }
            } catch (IOException e) {
                System.out.println("Не удалось восстановить данные задач");
            }
        }
    }

    private void restoreHistory() {
        file = historyFile;
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
                        task = new Task(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5])
                        );
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == EPIC) {
                        task = new Epic(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), new HashMap<>()
                        );
                    }
                    else if (TaskTypes.valueOf(tokens[5]) == SUB_TASK) {
                        task = new SubTask(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), TaskTypes.valueOf(tokens[5]), tokens[6]
                        );
                        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[6]);
                        parentTask.relatedSubTask.put(tokens[0], String.valueOf(TaskStages.valueOf(tokens[4])));
                        setEpicStatus(tokens[6]);
                    }

                    InMemoryHistoryManager.historyStorage.linkLast(task);
                }
            } catch (IOException e) {
                System.out.println("Не удалось восстановить данные истории");
            }
        }
    }

    private void saveTask(String saveMode) {

        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(
                        PATH + File.separator + "dataFile.csv", UTF_8)))
        ) {

            for (String entry : tasksStorage.keySet()) {
                writer.append(getTaskFormattedData(tasksStorage.get(entry).getTaskId()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные задач");
        }

        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(
                        PATH + File.separator + "historyFile.csv", UTF_8
                )))
        ) {
            Map<String, Node> list = InMemoryHistoryManager.historyRegister;

            for (String i : list.keySet()) {
                String newData = getTaskFormattedData(list.get(i).getTask().getTaskId());

                if (!newData.equals(taskContent)) {
                    writer.append(newData);
                    writer.newLine();
                }

            }
            if (!(taskContent == null) && !(saveMode.equals("newTask") || saveMode.equals("deleteTask"))) {
                writer.append(taskContent);
            }

        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные истории");
        }
    }

    /*public int getInitNumber() {
        File file = new File(PATH + File.separator + "dataFile.csv");
        int max = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
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
    }*/
}
