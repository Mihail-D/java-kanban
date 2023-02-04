package controls;

import exceptions.ManagerSaveException;
import org.jetbrains.annotations.NotNull;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private boolean dataFile;
    private boolean historyFile;
    private final static int SUBTASK_LINE_LENGTH = 6;

    public FileBackedTasksManager(boolean dataFile, boolean historyFile) {
        this.dataFile = dataFile;
        this.historyFile = historyFile;
        restoreTasks();
        restoreHistory();
    }

    @Override
    public void taskAdd(String @NotNull ... args) {
        try {
            super.taskAdd(args);
            saveTask();
        } catch (ManagerSaveException e) {
            System.out.println("Не удалось создать задачу.");
        }
    }

    @Override
    public String taskRetrieve(String taskKey) throws IOException {
        String oldData = super.getTaskFormattedData(taskKey);
        try {
            super.taskRetrieve(taskKey);
        } catch (ManagerSaveException e) {
            System.out.println("Не удалось получить данные.");
        }
        try {
            saveHistory(taskKey);
        } catch (ManagerSaveException e) {
            System.out.println("Не удалось сохранить историю.");
        }
        String newData = super.getTaskFormattedData(taskKey);
        lineOverwrite(oldData, newData, "dataFile.csv");
        lineOverwrite(oldData, newData, "historyFile.csv");

        return InMemoryTaskManager.taskContent;
    }

    @Override
    public void taskUpdate(String @NotNull ... args) throws IOException {
        String oldData = super.getTaskFormattedData(args[0]);
        try {
            super.taskUpdate(args);
        } catch (ManagerSaveException e) {
            System.out.println("Не удалось обновить данные.");
        }
        String newData = super.getTaskFormattedData(args[0]);
        lineOverwrite(oldData, newData, "dataFile.csv");
        lineOverwrite(oldData, newData, "historyFile.csv");
    }

    @Override
    public void taskDelete(String @NotNull ... args) throws IOException {
        String dataForErase = super.getTaskFormattedData(args[0]);
        String taskType = args[0].substring(0, 1);

        if (taskType.equals("t")) {
            lineErase("single", "dataFile.csv", dataForErase);
            lineErase("single", "historyFile.csv", dataForErase);
            super.taskDelete(args);
        }
        else if (taskType.equals("e")) {
            lineErase("epic", "dataFile.csv", dataForErase);
            lineErase("epic", "historyFile.csv", dataForErase);
            super.taskDelete(args);
        }
        else if (taskType.equals("s")) {
            String parentKey = dataForErase.split(",")[5];
            String oldEpicStatus = super.getTaskFormattedData(args[1]);
            lineErase("sub", "dataFile.csv", dataForErase);
            lineErase("sub", "historyFile.csv", dataForErase);
            assert oldEpicStatus != null;
            super.taskDelete(args);
            lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "dataFile.csv");
            lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "historyFile.csv");
        }
    }

    @Override
    public void tasksClear() throws IOException {
        super.tasksClear();
        lineErase("complete", "dataFile.csv");
        lineErase("complete", "historyFile.csv");
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

                    if (tokens[0].charAt(0) == 't') {
                        task = new Task(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4])
                        );
                    }
                    else if (tokens[0].charAt(0) == 'e') {
                        task = new Epic(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), new HashMap<>()
                        );
                    }
                    else if (tokens[0].charAt(0) == 's') {
                        task = new SubTask(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), tokens[5]
                        );
                        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[5]);
                        parentTask.relatedSubTask.put(tokens[0], String.valueOf(TaskStages.valueOf(tokens[4])));
                        setEpicStatus(tokens[5]);
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

                    if (tokens[0].charAt(0) == 't') {
                        task = new Task(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4])
                        );
                    }
                    else if (tokens[0].charAt(0) == 'e') {
                        task = new Epic(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), new HashMap<>()
                        );
                    }
                    else if (tokens[0].charAt(0) == 's') {
                        task = new SubTask(tokens[1], tokens[2], tokens[0], Boolean.parseBoolean(tokens[3]),
                                TaskStages.valueOf(tokens[4]), tokens[5]
                        );
                        Epic parentTask = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[5]);
                        parentTask.relatedSubTask.put(tokens[0], String.valueOf(TaskStages.valueOf(tokens[4])));
                        setEpicStatus(tokens[5]);
                    }

                    InMemoryHistoryManager.historyStorage.linkLast(task);
                }
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
            System.out.println(args[2]);

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
