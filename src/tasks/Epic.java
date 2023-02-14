package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {

    public HashMap<String, String> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, TaskTypes taskType, HashMap<String, String> relatedSubTask,
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