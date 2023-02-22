package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class Epic extends Task {

    public LinkedHashMap<String, SubTask> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, boolean isViewed, TaskStages taskStatus,
            TaskTypes taskType, LocalDateTime startTime, Duration duration,
            LinkedHashMap<String, SubTask> relatedSubTask
    ) {
        super(taskTitle, taskDescription, isViewed, taskStatus, taskType, startTime, duration);
        this.relatedSubTask = relatedSubTask;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime endTime = LocalDateTime.now();

        for (SubTask i : relatedSubTask.values()) {
            if (i.getEndTime().isAfter(endTime)) {
                endTime = i.getEndTime();
            }
        }
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic RelatedSubTask = " + relatedSubTask + super.toString() + "\n";
    }
}