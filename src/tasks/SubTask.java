package tasks;

public class SubTask extends Task {
    String parentId;

    public SubTask(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, String parentId
    ) {
        super(taskTitle, taskDescription, taskId, isViewed, taskStatus);
        this.parentId = parentId;
    }
    @Override
    public String toString() {
        return "SubTask {" +
                "ParentId='" + parentId + '\'' + " " + super.toString() + '}';
    }
}