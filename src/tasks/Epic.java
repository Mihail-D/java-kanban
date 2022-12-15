package tasks;

import java.util.HashMap;

public class Epic extends Task {
    public HashMap<String, String> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, String taskId, TaskStages taskStatus,
            HashMap<String, String> relatedSubTask
    ) {
        super(taskTitle, taskDescription, taskId, taskStatus);
        this.relatedSubTask = relatedSubTask;
    }
}