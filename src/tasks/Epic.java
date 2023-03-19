package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> relatedSubTasks;
    protected LocalDateTime endTime;

public Epic(String taskTitle, String taskDescription, TaskStatus taskStatus) {
        super(taskTitle, taskDescription, taskStatus);
        relatedSubTasks = new ArrayList<>();
        endTime = this.taskStartTime.plusMinutes(1);
    }

    public void addChild(Integer subtaskId) {
        relatedSubTasks.add(subtaskId);
    }

    public void removeSubtask(Integer taskKey) {
        relatedSubTasks.remove(taskKey);
    }

    public ArrayList<Integer> getRelatedSubTasks() {
        return relatedSubTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return  taskKey +
                "," + TaskType.EPIC +
                "," + taskTitle +
                "," + taskStatus +
                "," + taskDescription;
    }
}
