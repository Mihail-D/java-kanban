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

    @Override
    public void taskAdd(String... args) {
        super.taskAdd(args);
        saveTask();
    }

    public String taskGet(String taskKey) throws IOException {
        String oldData = super.getTaskFormattedData(taskKey);
        super.taskRetrieve(taskKey);
        saveHistory(taskKey);
        String newData = super.getTaskFormattedData(taskKey);
        dataStorageOverwrite(oldData, newData);

        System.out.println(InMemoryTaskManager.taskContent); // TODO

        return InMemoryTaskManager.taskContent;
    }

    public void taskEdit(String... args) throws IOException {
        String oldData = super.getTaskFormattedData(args[0]);
        super.taskUpdate(args);
        String newData = super.getTaskFormattedData(args[0]);
        dataStorageOverwrite(oldData, newData);
    }

    public void recordDelete(String... args) throws IOException {
        String oldData = super.getTaskFormattedData(args[0]);
        String taskType = args[0].substring(0, 1);

        if (taskType.equals("t") || taskType.equals("s")) {
            lineErase("single", "dataStorage.csv", oldData);
            lineErase("single", "historyStorage.csv", oldData);
        }
        else if (taskType.equals("e")) {
            lineErase("epic", "dataStorage.csv", oldData);
            lineErase("epic", "historyStorage.csv", oldData);
        }

        super.taskDelete(args);
        // s.2, sdg, sdg, true, NEW, e.1
    }

    //  *********************************************************************************

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
        System.out.println("from restoreTasks (tasksStorage) \n " + InMemoryTaskManager.tasksStorage); // TODO

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
        //System.out.println("from restoreHistory (historyStorage.getSize) " + InMemoryHistoryManager.historyStorage
        // .getSize()); // TODO
        System.out.println("from restoreHistory (historyRegister) " + InMemoryHistoryManager.historyRegister); // TODO   

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
                    lineErase("partial", "historyStorage.csv", line);
                }
            }

            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void dataStorageOverwrite(String oldData, String newData) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(
                Path.of(PATH + File.separator + "dataStorage.csv"), StandardCharsets.UTF_8));
        String[] tokens = oldData.split(",");
        int subtaskLineLength = 6;

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).equals(oldData)) {
                fileContent.set(i, newData);
                break;
            }
            if (tokens.length == subtaskLineLength) {
                String parentKey = tokens[tokens.length - 1];
                String parentContent = super.getTaskFormattedData(parentKey);
                for (int j = 0; j < fileContent.size(); j++) {
                    if (fileContent.get(j).startsWith(parentKey)) {
                        fileContent.set(j, parentContent);
                    }
                }
            }
        }

        Files.write(Path.of(PATH + File.separator + "dataStorage.csv"), fileContent, StandardCharsets.UTF_8);
    }

    public void lineErase(String... args) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(
                Path.of(PATH + File.separator + args[1]), StandardCharsets.UTF_8));
        String[] tokens = args[2].split(",");

        if (args[0].equals("complete")) {
            fileContent.clear();
        }
        else if (args[0].equals("single")) {
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).equals(args[2])) {
                    fileContent.remove(i);
                    break;
                }
            }
        }
        else if (args[0].equals("epic")) {
            Epic task = (Epic) InMemoryTaskManager.tasksStorage.get(tokens[0]);
            Map<String, String> relativeTasks = task.relatedSubTask;
            int subtaskLineLength = 6;

            for (String i : new ArrayList<>(fileContent)) {
                String[] arr = i.split(",");
                if (arr.length == subtaskLineLength && relativeTasks.containsKey(arr[0])) {
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
