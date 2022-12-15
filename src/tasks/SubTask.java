package tasks;

public class SubTask extends AbstractTask {
    String parentId;

    public SubTask(String taskTitle, String taskDescription, String taskId, TaskStages taskStatus, String parentId) {
        super(taskTitle, taskDescription, taskId, taskStatus);
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "parentId='" + parentId + '\'' + " " + super.toString() + '}';
    }
}

