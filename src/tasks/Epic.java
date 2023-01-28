package tasks;

import java.util.HashMap;

public class Epic extends Task {

    public HashMap<String, String> relatedSubTask;

    public Epic(
            String taskTitle, String taskDescription, String taskId, boolean isViewed,
            TaskStages taskStatus, HashMap<String, String> relatedSubTask
    ) {
        super(taskTitle, taskDescription, taskId, isViewed, taskStatus);
        this.relatedSubTask = relatedSubTask;
    }

    @Override
    public String toString() {
        return "Epic RelatedSubTask='" + relatedSubTask + super.toString() + "\n";
    }
}