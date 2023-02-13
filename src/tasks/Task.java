package tasks;

import java.time.LocalDateTime;

public class Task {

    String taskTitle;
    private String taskDescription;
    private String taskId;
    private boolean isViewed;
    private TaskStages taskStatus;
    private TaskTypes taskType;
    private LocalDateTime startTime;

    public Task(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, TaskTypes taskType, LocalDateTime startTime
    ) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.isViewed = isViewed;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.startTime = startTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (!getTaskTitle().equals(task.getTaskTitle())) {
            return false;
        }
        if (!getTaskDescription().equals(task.getTaskDescription())) {
            return false;
        }
        if (!getTaskId().equals(task.getTaskId())) {
            return false;
        }
        return getTaskStatus() == task.getTaskStatus();
    }

    @Override
    public int hashCode() {
        int result = getTaskTitle().hashCode();
        result = 31 * result + getTaskDescription().hashCode();
        result = 31 * result + getTaskId().hashCode();
        result = 31 * result + getTaskStatus().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return " MainTask Title = " + taskTitle +
                " Description = " + taskDescription +
                " Id = " + taskId + " isViewed = " + isViewed + " Status = " + taskStatus + "***";
    }
}