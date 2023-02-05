package controls;

import exceptions.ManagerSaveException;
import org.jetbrains.annotations.NotNull;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
        String oldData = super.getTaskFormattedData(taskKey);
        super.taskRetrieve(taskKey);
        saveHistory(taskKey);
        String newData = super.getTaskFormattedData(taskKey);

        try {
            lineOverwrite(oldData, newData, "dataFile.csv");
            lineOverwrite(oldData, newData, "historyFile.csv");
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные");
        }

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(String @NotNull ... args) {
        String oldData = super.getTaskFormattedData(args[0]);
        super.taskUpdate(args);
        String newData = super.getTaskFormattedData(args[0]);

        try {
            lineOverwrite(oldData, newData, "dataFile.csv");
            lineOverwrite(oldData, newData, "historyFile.csv");
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные");
        }
    }

    @Override
    public void taskDelete(String @NotNull ... args) {
        String dataForErase = super.getTaskFormattedData(args[0]);
        String taskType = dataForErase.split(",")[5];

        try {
            if (taskType.equals("TASK")) {
                lineErase("single", "dataFile.csv", dataForErase);
                lineErase("single", "historyFile.csv", dataForErase);
            }
            else if (taskType.equals("EPIC")) {
                lineErase("epic", "dataFile.csv", dataForErase);
                lineErase("epic", "historyFile.csv", dataForErase);
            }
            else if (taskType.equals("SUB_TASK")) {
                String parentKey = dataForErase.split(",")[6];
                String oldEpicStatus = super.getTaskFormattedData(args[1]);
                lineErase("sub", "dataFile.csv", dataForErase);
                lineErase("sub", "historyFile.csv", dataForErase);
                assert oldEpicStatus != null;
                lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "dataFile.csv");
                lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "historyFile.csv");
            }
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось удалить данные");
        }

        super.taskDelete(args);
    }

    @Override
    public void tasksClear() {
        super.tasksClear();
        try {
            lineErase("complete", "dataFile.csv");
            lineErase("complete", "historyFile.csv");
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось удалить данные");
        }
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

                System.out.println(InMemoryTaskManager.tasksStorage.size()); // TODO

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
        try {
            String filename = PATH + File.separator + "dataFile.csv";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные задач");
        }
    }

    private void saveHistory(@NotNull String taskKey) {
        String filename = PATH + File.separator + "historyFile.csv";
        int referenceKey = Integer.parseInt(taskKey.substring(2));

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");

                if (referenceKey == Integer.parseInt(tokens[0].substring(2))) {
                    lineErase("single", "historyFile.csv", line);
                }
            }

            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (ManagerSaveException | IOException e) {
            System.out.println("Не удалось сохранить данные истории");
        }
    }

    private void lineOverwrite(@NotNull String oldData, String newData, String fileName) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(
                Path.of(PATH + File.separator + fileName), StandardCharsets.UTF_8));
        String[] tokens = oldData.split(",");

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).equals(oldData)) {
                fileContent.set(i, newData);
                break;
            }
            if (tokens.length == SUBTASK_LINE_LENGTH) {
                String parentKey = tokens[tokens.length - 1];
                String parentContent = super.getTaskFormattedData(parentKey);
                for (int j = 0; j < fileContent.size(); j++) {
                    if (fileContent.get(j).startsWith(parentKey)) {
                        fileContent.set(j, parentContent);
                    }
                }
            }
        }

        Files.write(Path.of(PATH + File.separator + fileName), fileContent, StandardCharsets.UTF_8);
    }

    private void lineErase(String @NotNull ... args) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(
                Path.of(PATH + File.separator + args[1]), StandardCharsets.UTF_8));

        if (args[0].equals("complete")) {
            fileContent.clear();
        }

        if (args[0].equals("single")) {
            String[] tokens = args[2].split(",");
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).equals(args[2])) {
                    fileContent.remove(i);
                    break;
                }
            }
        }

        else if (args[0].equals("sub")) {
            String[] tokens = args[2].split(",");

            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).equals(args[2])) {
                    fileContent.remove(i);
                    break;
                }
            }
        }

        else if (args[0].equals("epic")) {
            String[] tokens = args[2].split(",");
            Epic task = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[0]);

            for (String i : new ArrayList<>(fileContent)) {
                String[] arr = i.split(",");
                if (arr.length == SUBTASK_LINE_LENGTH && task.relatedSubTask.containsKey(arr[0])) {
                    fileContent.remove(i);
                }
            }

            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).equals(args[2])) {
                    fileContent.remove(i);
                    break;
                }
            }
        }
        Files.write(Path.of(PATH + File.separator + args[1]), fileContent, StandardCharsets.UTF_8);
    }
}
