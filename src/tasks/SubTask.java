package tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {

    String parentId;

    public SubTask(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, TaskTypes taskType, String parentId, LocalDateTime startTime, long duration
    ) {
        super(taskTitle, taskDescription, taskId, isViewed, taskStatus, taskType, startTime, duration);
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