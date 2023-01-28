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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public String taskGet(String taskKey) {
        super.taskRetrieve(taskKey);
        saveHistory(taskKey);
        System.out.println(InMemoryTaskManager.taskContent); // TODO

        return InMemoryTaskManager.taskContent;
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
        System.out.println("from restoreHistory (historyStorage.getSize) " + InMemoryHistoryManager.historyStorage.getSize()); // TODO
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
                    return;
                }
            }

            FileWriter fw = new FileWriter(filename, true);
            fw.write(taskContent + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void lineEdit(String lineOld, String lineNew) throws IOException {
        List<String> fileContent = new ArrayList<>(Files.readAllLines(
                Path.of(PATH + File.separator + "dataStorage.csv"), StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).equals(lineOld)) {
                fileContent.set(i, lineNew);
                break;
            }
        }

        Files.write(Path.of(PATH + File.separator + "dataStorage.csv"), fileContent, StandardCharsets.UTF_8);
    }

}
