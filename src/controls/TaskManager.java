package controls;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd(String... args);

    void taskUpdate(String... args);

    String taskRetrieve(String taskKey);

    List<ArrayList<String>> collectAllTasks();

    List<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String... args);

    void tasksClear();
}


