package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    protected int taskKey;
    protected String taskTitle;
    protected String taskDescription;
    protected TaskStatus taskStatus;
    protected Duration taskDuration;
    protected LocalDateTime taskStartTime;

    public Task(String taskTitle, String taskDescription, TaskStatus taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskStartTime = getTaskInitTime();
    }

    public Task(String taskTitle, String taskDescription, TaskStatus taskStatus, Duration taskDuration) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskDuration = taskDuration;
        this.taskStartTime = getTaskInitTime();
    }

    public Task(String taskTitle, String taskDescription, TaskStatus taskStatus, Duration taskDuration,
                LocalDateTime taskStartTime) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskDuration = taskDuration;
        this.taskStartTime = taskStartTime;
    }

    public int getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(int taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(Duration taskDuration) {
        this.taskDuration = taskDuration;
    }

    public LocalDateTime getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(LocalDateTime taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public LocalDateTime getTaskEndTime() {
        return taskStartTime.plus(taskDuration);
    }

    public static DateTimeFormatter getTaskTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH.mm.dd_MM.yyyy");
    }

    public static LocalDateTime getTaskInitTime() {
        return LocalDateTime.parse("01.01.01_01.2200", getTaskTimeFormatter());
    }

    @Override
    public String toString() {
        return taskKey +
                "," + TaskType.TASK +
                "," + taskTitle +
                "," + taskStatus +
                "," + taskDescription +
                "," + taskDuration +
                "," + taskStartTime.format(getTaskTimeFormatter()) +
                ",";
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

        if (getTaskKey() != task.getTaskKey()) {
            return false;
        }
        if (!taskTitle.equals(task.taskTitle)) {
            return false;
        }
        if (!getTaskDescription().equals(task.getTaskDescription())) {
            return false;
        }
        if (getTaskStatus() != task.getTaskStatus()) {
            return false;
        }
        if (!getTaskDuration().equals(task.getTaskDuration())) {
            return false;
        }
        return getTaskStartTime().equals(task.getTaskStartTime());
    }

    @Override
    public int hashCode() {
        int result = getTaskKey();
        result = 31 * result + taskTitle.hashCode();
        result = 31 * result + getTaskDescription().hashCode();
        result = 31 * result + getTaskStatus().hashCode();
        result = 31 * result + getTaskDuration().hashCode();
        result = 31 * result + getTaskStartTime().hashCode();
        return result;
    }
}
