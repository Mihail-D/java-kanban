package tasks;

public class SubTask extends AbstractTask {
    private String subTaskId;

    public SubTask(
            String taskTitle, String taskDescription, String parentId, TaskStages taskStatus,
            String subTaskId
    ) {
        super(taskTitle, taskDescription, parentId, taskStatus);
        this.subTaskId = subTaskId;
    }

    public String getSubTaskId() {
        return subTaskId;
    }
    @Override
    public String toString() {
        return "SubTask{subTaskId=" + subTaskId + " " + super.toString();
    }
}

