package controls;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd();

    void taskUpdate(String... args);

    String taskRetrieve(String taskKey);

    ArrayList<String>[] collectAllTasks();

    ArrayList<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String... args);

    void tasksClear();

    List<Task> getHistory();
}


