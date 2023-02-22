package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    String parentId;

    public SubTask(
            String taskTitle, String taskDescription, boolean isViewed, TaskStages taskStatus,
            TaskTypes taskType, LocalDateTime startTime, Duration duration, String parentId
    ) {
        super(taskTitle, taskDescription, isViewed, taskStatus, taskType, startTime, duration);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "SubTask " + "ParentId = " + parentId + " " + super.toString() + "\n";
    }
}