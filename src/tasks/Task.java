package tasks;

public class Task {
    String taskTitle;
    private String taskDescription;
    private String taskId;
    private TaskStages taskStatus;

    public Task(String taskTitle, String taskDescription, String taskId, TaskStages taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
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

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public void setTaskStatus(TaskStages taskStatus) {
                this.taskStatus = taskStatus;
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
        return " MainTask { Title='" + taskTitle + '\'' +
                ", Description='" + taskDescription + '\'' +
                ", Id=" + taskId +
                ", Status='" + taskStatus;
    }
}