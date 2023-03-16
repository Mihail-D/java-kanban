package tasks;

import java.time.Duration;
import java.time.Instant;

public class SubTask extends Task {

    private int parentTaskKey;

    public SubTask(String taskTitle, String taskDescription, TaskStatus taskStatus,
                   Instant taskStartTime, Duration taskDuration) {
        super(taskTitle, taskDescription, taskStatus, taskStartTime, taskDuration);
        setTaskEndTime(taskStartTime.plus(taskDuration));
    }

    public SubTask(
            Integer taskKey, String taskTitle, String taskDescription, TaskStatus taskStatus, Instant taskStartTime,
            Duration taskDuration, Instant tmpTimeTaskWasUpdated, Integer parentTaskKey
    ) {
        super(taskKey, taskTitle, taskDescription, taskStatus, taskStartTime, taskDuration, tmpTimeTaskWasUpdated);
        this.parentTaskKey = parentTaskKey;
        setTaskEndTime(taskStartTime.plus(taskDuration));
    }

    public int getParentKey() {
        return parentTaskKey;
    }

    public void setRelatedSubtasks(int parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }

    @Override
    public String toString() {
        return "SubTask;"
                + getTaskKey() + ";"
                + parentTaskKey + ";"
                + getTaskTitle() + ";"
                + getTaskDescription() + ";"
                + getTaskStatus() + ";"
                + getTaskStartTime() + ";"
                + getTaskDuration() + ";"
                + getTaskTimeUpdateCheck();
    }
}
