package tasks;

public abstract class AbstractTask {
    String taskTitle;
    private String taskDescription;
    private String taskId;
    private TaskStages taskStatus;

    public AbstractTask(String taskTitle, String taskDescription, String taskId, TaskStages taskStatus) {
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
    public void setTaskStatus(String taskStatus) {
        switch (taskStatus) {
            case "NEW":
                this.taskStatus = TaskStages.NEW;
                break;
            case "IN_PROGRESS":
                this.taskStatus = TaskStages.IN_PROGRESS;
                break;
            case "DONE":
                this.taskStatus = TaskStages.DONE;
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractTask that = (AbstractTask) o;

        if (getTaskId() != that.getTaskId()) {
            return false;
        }
        if (!getTaskTitle().equals(that.getTaskTitle())) {
            return false;
        }
        if (!getTaskDescription().equals(that.getTaskDescription())) {
            return false;
        }
        return getTaskStatus().equals(that.getTaskStatus());
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
        return "Title='" + taskTitle + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskId=" + taskId +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
