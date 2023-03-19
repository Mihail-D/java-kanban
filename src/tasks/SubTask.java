package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    public int parentTaskKey;

    public SubTask(
            String taskTitle, String taskDescription, TaskStatus taskStatus, Duration taskDuration,
            LocalDateTime taskStartTime, int parentTaskKey
    ) {
        super(taskTitle, taskDescription, taskStatus, taskDuration, taskStartTime);
        this.parentTaskKey = parentTaskKey;
    }

    public int getParentKey() {
        return parentTaskKey;
    }

    public void setParentKey(int parentTaskKey) {
        this.parentTaskKey = parentTaskKey;
    }

    @Override
    public String toString() {

        return taskKey +
                "," + TaskType.SUB_TASK +
                "," + taskTitle +
                "," + taskStatus +
                "," + taskDescription +
                "," + taskDuration +
                "," + taskStartTime.format(getTaskTimeFormatter()) +
                "," + parentTaskKey;
    }
}
