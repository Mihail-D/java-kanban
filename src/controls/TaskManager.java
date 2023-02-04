package controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd(String... args);

    void taskUpdate(String... args) throws IOException;

    String taskRetrieve(String taskKey) throws IOException;

    List<ArrayList<String>> collectAllTasks();

    List<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String... args) throws IOException;

    void tasksClear() throws IOException;

}


