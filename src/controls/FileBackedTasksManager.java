package controls;

import exceptions.ManagerSaveException;
import org.jetbrains.annotations.NotNull;
import tasks.*;

import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tasks.TaskTypes.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private boolean dataFile;
    private boolean historyFile;
    private final static int SUBTASK_LINE_LENGTH = 7;

    public FileBackedTasksManager(boolean dataFile, boolean historyFile) {
        this.dataFile = dataFile;
        this.historyFile = historyFile;
        restoreTasks();
        restoreHistory();
    }

    @Override
    public void taskAdd(String @NotNull ... args) {
        super.taskAdd(args);
        saveTask();
    }

    @Override
    public String taskRetrieve(String taskKey) {
        System.out.println(InMemoryHistoryManager.historyRegister); // TODO   
        super.taskRetrieve(taskKey);
        System.out.println(InMemoryHistoryManager.historyRegister); // TODO
        saveTask();

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(String @NotNull ... args) {
        String oldData = super.getTaskFormattedData(args[0]);
        super.taskUpdate(args);
        String newData = super.getTaskFormattedData(args[0]);

    }

    @Override
    public void taskDelete(String @NotNull ... args) {
        String dataForErase = super.getTaskFormattedData(args[0]);
        String taskType = dataForErase.split(",")[5];

        super.taskDelete(args);
    }

    @Override
    public void tasksClear() {
        super.tasksClear();

    }

    private void restoreTasks() {
        File file = new File(PATH + File.separator + "dataFile.csv");
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
            } catch (ManagerSaveException | IOException e) {
                System.out.println("Не удалось восстановить данные задач");
            }
        }
    }

    private void restoreHistory() {
        File file = new File(PATH + File.separator + "historyFile.csv");
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
                System.out.println(InMemoryHistoryManager.historyStorage.getSize()); // TODO
                System.out.println(InMemoryHistoryManager.historyRegister.size()); // TODO

            } catch (ManagerSaveException | IOException e) {
                System.out.println("Не удалось восстановить данные истории");
            }
        }
    }

    private void saveTask() {

        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(
                        PATH + File.separator + "dataFile.csv", UTF_8)))
        ) {

            for (String entry : tasksStorage.keySet()) {
                writer.append(getTaskFormattedData(tasksStorage.get(entry).getTaskId()));
                writer.newLine();
            }
            //writer.append(taskContent);
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные задач");
        }



        try (
                final BufferedWriter writer = new BufferedWriter((new FileWriter(
                        PATH + File.separator + "historyFile.csv", UTF_8
                )))
        ) {
            List<Task> list = new LinkedList<>(InMemoryHistoryManager.historyStorage.getTasks());

            for (int i = 0; i < list.size(); i++) {
                writer.append(getTaskFormattedData(list.get(i).getTaskId()));
                writer.newLine();
            }


        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные истории");
        }
    }

}
