package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Epic extends Task {

    public LinkedHashMap<String, SubTask> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, TaskTypes taskType, LinkedHashMap<String, SubTask> relatedSubTask,
            LocalDateTime startTime, Duration duration
    ) {
        super(taskTitle, taskDescription, taskId, isViewed, taskStatus, taskType, startTime, duration);
        this.relatedSubTask = relatedSubTask;
    }

    @Override
    public String toString() {
        return "Epic RelatedSubTask = " + relatedSubTask + super.toString() + "\n";
    }
}