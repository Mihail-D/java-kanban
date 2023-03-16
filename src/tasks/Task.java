package tasks;

import java.time.Duration;
import java.time.Instant;

public class Task {

    private String taskTitle;
    private String taskDescription;
    private Integer taskKey;
    private TaskStatus taskStatus;
    private final Instant taskStartTime;
    private Duration taskDuration;
    private Instant taskEndTime;
    private static Instant taskTimeUpdateCheck;
    public static int taskKeyCounter = 0;

    public Task(
            String taskTitle, String taskDescription, TaskStatus taskStatus, Instant taskStartTime,
            Duration taskDuration
    ) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskStartTime = taskStartTime;
        this.taskDuration = taskDuration;
        this.taskEndTime = taskStartTime.plus(taskDuration);
    }

    public Task(
            Integer taskKey, String taskTitle, String taskDescription, TaskStatus taskStatus,
            Instant taskStartTime,
            Duration taskDuration, Instant tmpTimeTaskWasUpdated
    ) {
        this.taskKey = taskKey;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskStartTime = taskStartTime;
        this.taskDuration = taskDuration;
        taskTimeUpdateCheck = tmpTimeTaskWasUpdated;
        this.taskEndTime = taskStartTime.plus(taskDuration);
    }

    public Integer getTaskKey() {
        return taskKey;
    }

    public Integer setTaskKey() {
        taskUpdateTime();
        taskKeyCounter++;
        this.taskKey = taskKeyCounter;
        return this.taskKey;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        taskUpdateTime();
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        taskUpdateTime();
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        taskUpdateTime();
        this.taskStatus = taskStatus;
        if (taskStatus.equals(TaskStatus.DONE)) {
            setTaskEndTime();
        }
    }

    public Instant getTaskStartTime() {
        return taskStartTime;
    }

    public Duration getTaskDuration() {
        return taskDuration;
    }

    public static Instant getTaskTimeUpdateCheck() {
        return taskTimeUpdateCheck;
    }

    public static void taskUpdateTime() {
        Task.taskTimeUpdateCheck = Instant.now();
    }

    public void setTaskEndTime(Instant taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public Instant getTaskEndTime() {
        return this.taskEndTime;
    }

    public void setTaskEndTime() {
        taskDuration = Duration.between(taskStartTime, taskTimeUpdateCheck);
        this.taskEndTime = taskStartTime.plus(taskDuration);
    }

    public static void setIdCounter(int taskKeyCounter) {
        Task.taskKeyCounter = taskKeyCounter;
    }

    public void setTaskKey(Integer taskKey) {
        this.taskKey = taskKey;
    }

    @Override
    public String toString() {
        return "Task;"
                + taskKey + ";"
                + taskTitle + ";"
                + taskDescription + ";"
                + taskStatus + ";"
                + taskStartTime + ";"
                + taskDuration + ";"
                + taskTimeUpdateCheck;
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

        if (!getTaskKey().equals(task.getTaskKey())) {
            return false;
        }
        if (!taskTitle.equals(task.taskTitle)) {
            return false;
        }
        if (!taskDescription.equals(task.taskDescription)) {
            return false;
        }
        if (taskStatus != task.taskStatus) {
            return false;
        }
        if (!taskStartTime.equals(task.taskStartTime)) {
            return false;
        }
        if (!taskDuration.equals(task.taskDuration)) {
            return false;
        }
        return taskEndTime.equals(task.taskEndTime);
    }

    @Override
    public int hashCode() {
        int result = getTaskKey().hashCode();
        result = 31 * result + taskTitle.hashCode();
        result = 31 * result + taskDescription.hashCode();
        result = 31 * result + taskStatus.hashCode();
        result = 31 * result + taskStartTime.hashCode();
        result = 31 * result + taskDuration.hashCode();
        result = 31 * result + taskEndTime.hashCode();
        return result;
    }
}