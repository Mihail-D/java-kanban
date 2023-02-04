package controls;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd(String @NotNull ... args);

    void taskUpdate(String @NotNull ... args);

    String taskRetrieve(String taskKey);

    List<ArrayList<String>> collectAllTasks();

    List<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String @NotNull ... args);

    void tasksClear();
}


