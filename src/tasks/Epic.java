package tasks;

import java.util.HashMap;

public class Epic extends AbstractTask {
    public HashMap<String, String> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, String taskId, TaskStages taskStatus,
            HashMap<String, String> relatedSubTask
    ) {
        super(taskTitle, taskDescription, taskId, taskStatus);
        this.relatedSubTask = relatedSubTask;
    }

    @Override
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    @Override
    public String toString() {
        return "Epic{ relatedSubTask" + relatedSubTask + " " + super.toString();
    }
}
