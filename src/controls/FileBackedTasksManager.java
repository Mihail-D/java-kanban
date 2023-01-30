package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager() throws IOException {
        String PATH = InMemoryTaskManager.PATH;

        Files.createDirectories(Paths.get(PATH));
        File dataStorage = new File(PATH + File.separator + "dataStorage.csv");
        dataStorage.createNewFile();
        File historyStorage = new File(PATH + File.separator + "historyStorage.csv");
        historyStorage.createNewFile();
    }

    private final static int SUBTASK_LINE_LENGTH = 6;
    
    public void dataAdd(String... args) {
        super.taskAdd(args);
        saveTask();
    }

    public String dataGet(String taskKey) throws IOException {
        String oldData = super.getTaskFormattedData(taskKey);
        super.taskRetrieve(taskKey);
        saveHistory(taskKey);
        String newData = super.getTaskFormattedData(taskKey);
        lineOverwrite(oldData, newData, "historyStorage.csv");

        return InMemoryTaskManager.taskContent;
    }

    public void dataEdit(String... args) throws IOException {
        String oldData = super.getTaskFormattedData(args[0]);
        super.taskUpdate(args);
        String newData = super.getTaskFormattedData(args[0]);
        lineOverwrite(oldData, newData, "dataStorage.csv");
        lineOverwrite(oldData, newData, "historyStorage.csv");
    }

    public void dataDelete(String... args) throws IOException {
        String dataForErase = super.getTaskFormattedData(args[0]);
        String[] arr = dataForErase.split(",");
        
        String parentKey = null;
        String oldEpicStatus = null;
        if (arr.length == 6) {
            parentKey = dataForErase.split(",")[5];
            oldEpicStatus = super.getTaskFormattedData(args[1]);
        }

        String taskType = args[0].substring(0, 1);

        super.taskDelete(args);

        if (taskType.equals("t")) {
            lineErase("single", "dataStorage.csv", dataForErase);
            lineErase("single", "historyStorage.csv", dataForErase);
        }
        else if (taskType.equals("s")) {                // TODO args[0] key; args[1] parentKey
            lineErase("sub", "dataStorage.csv", dataForErase);
            lineErase("sub", "historyStorage.csv", dataForErase);
            assert oldEpicStatus != null;
            lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "dataStorage.csv");
            lineOverwrite(oldEpicStatus, super.getTaskFormattedData(parentKey), "historyStorage.csv");
        }
        else if (taskType.equals("e")) {
            lineErase("epic", "dataStorage.csv", dataForErase);
            lineErase("epic", "historyStorage.csv", dataForErase);
        }

    }

    public void dataClear() throws IOException {
        super.tasksClear();
        lineErase("complete", "dataStorage.csv");
        lineErase("complete", "historyStorage.csv");
    }

    public void restoreTasks() {
        File file = new File(PATH + File.separator + "dataStorage.csv");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreHistory() {
        File file = new File(PATH + File.separator + "historyStorage.csv");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("historyStorage " + InMemoryHistoryManager.historyStorage.getSize());  // TODO
        System.out.println("historyRegister " + InMemoryHistoryManager.historyRegister.size());   // TODO
        System.out.println("historyReport " + InMemoryHistoryManager.historyReport.size());       // TODO

    }

    public void saveTask() {
        try {
            String filename = PATH + File.separator + "dataStorage.csv";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void saveHistory(String taskKey) {
        String filename = PATH + File.separator + "historyStorage.csv";
        int referenceKey = Integer.parseInt(taskKey.substring(2));
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");

                if (referenceKey == Integer.parseInt(tokens[0].substring(2))) {
                    lineErase("single", "historyStorage.csv", line);
                }
            }

            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void lineOverwrite(String oldData, String newData, String fileName) throws IOException {
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

    public void lineErase(String... args) throws IOException {
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
            System.out.println(args[2]); // TODO

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
            Map<String, String> relativeTasks = task.relatedSubTask;

            for (String i : new ArrayList<>(fileContent)) {
                String[] arr = i.split(",");
                if (arr.length == SUBTASK_LINE_LENGTH && relativeTasks.containsKey(arr[0])) {
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
