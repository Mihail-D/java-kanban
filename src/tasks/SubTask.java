package tasks;

public class SubTask extends Task {

    String parentId;

    public SubTask(
            String taskTitle, String taskDescription, boolean isViewed, TaskStages taskStatus,
            TaskTypes taskType, String parentId
    ) {
        super(taskTitle, taskDescription, isViewed, taskStatus, taskType);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "SubTask " + "ParentId = " + parentId + " " + super.toString() + "\n";
    }
}