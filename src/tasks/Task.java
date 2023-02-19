package tasks;

import java.time.*;

public class Task {

    String taskTitle;
    private String taskDescription;
    private String taskId;
    private boolean isViewed;
    private TaskStages taskStatus;
    private TaskTypes taskType;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, TaskTypes taskType
    ) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.isViewed = isViewed;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    public TaskTypes getTaskType() {
        return taskType;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStages getTaskStatus() {
        return taskStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setViewed() {
        isViewed = true;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setTaskStatus(TaskStages taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }

        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (isViewed() != task.isViewed()) {
            return false;
        }
        if (!getTaskTitle().equals(task.getTaskTitle())) {
            return false;
        }
        if (!getTaskDescription().equals(task.getTaskDescription())) {
            return false;
        }
        if (!getTaskId().equals(task.getTaskId())) {
            return false;
        }
        if (getTaskStatus() != task.getTaskStatus()) {
            return false;
        }
        if (getTaskType() != task.getTaskType()) {
            return false;
        }
        if (!getStartTime().equals(task.getStartTime())) {
            return false;
        }
        return getDuration().equals(task.getDuration());
    }

    @Override
    public int hashCode() {
        int result = getTaskTitle().hashCode();
        result = 31 * result + getTaskDescription().hashCode();
        result = 31 * result + getTaskId().hashCode();
        result = 31 * result + (isViewed() ? 1 : 0);
        result = 31 * result + getTaskStatus().hashCode();
        result = 31 * result + getTaskType().hashCode();
        result = 31 * result + getStartTime().hashCode();
        result = 31 * result + getDuration().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return " MainTask Title = " + taskTitle +
                " Description = " + taskDescription +
                " Id = " + taskId + " isViewed = " + isViewed + " Status = " + taskStatus
                + " " + startTime + "***";
    }
}