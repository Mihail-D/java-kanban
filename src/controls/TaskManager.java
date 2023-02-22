package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void taskAdd(Task task);

    void epicAdd(Epic epic);

    void subTaskAdd(SubTask subTask);

    void taskUpdate(
            String taskKey, String taskTitle, String taskDescription,
            String taskStatus, LocalDateTime startTime, Duration duration
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


