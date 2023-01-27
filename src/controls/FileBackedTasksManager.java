package controls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager() throws IOException {
        String path = "./src/data";

        Files.createDirectories(Paths.get(path));
        Files.createFile(Path.of(path + File.separator + "dataStorage.csv"));
        Files.createFile(Path.of(path + File.separator + "historyStorage.csv"));
    }
}
