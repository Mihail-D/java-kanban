package controls;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd(
            String taskTitle, String taskDescription, String startTime,
            Duration duration
    );

    void epicAdd(String taskTitle, String taskDescription);

    void subTaskAdd(
            String taskTitle, String taskDescription, String parentKey,
            String time, Duration duration
    );

    void taskUpdate(
            String taskKey, String taskTitle, String taskDescription,
            String taskStatus, String startTime, Duration duration
    );

    void epicUpdate(String taskKey, String taskTitle, String taskDescription);

    void subTaskUpdate(
            String taskKey, String taskTitle, String taskDescription, String taskStatus,
            String parentKey, String startTime, Duration duration
    );

    String taskRetrieve(String taskKey);

    List<ArrayList<String>> collectAllTasks();

    List<String> collectEpicSubtasks(String taskKey);

    void taskDelete(String taskKey);

    void tasksClear();
}


