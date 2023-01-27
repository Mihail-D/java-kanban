package controls;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager() throws IOException {
        String path = "./src/data";

        Files.createDirectories(Paths.get(path));
        File dataStorage = new File(path + File.separator + "dataStorage.csv");
        dataStorage.createNewFile();
        File historyStorage = new File(path + File.separator + "historyStorage.csv");
        historyStorage.createNewFile();
    }

}
