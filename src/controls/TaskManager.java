package controls;

import java.util.ArrayList;

public interface TaskManager {

    void taskUpdate(String... args);

    String taskRetrieve(String taskKey);

    ArrayList<String>[] collectAllTasks();

    ArrayList<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String... args);

    void tasksClear();
}


