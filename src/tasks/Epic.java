package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public class Epic extends Task {

    private final Set<SubTask> relatedSubtasks;

    public Epic(
            String taskTitle, String taskDescription, TaskStatus taskStatus, Instant taskStartTime,
            Duration taskDuration
    ) {
        super(taskTitle, taskDescription, taskStatus, taskStartTime, taskDuration);
        this.relatedSubtasks = new LinkedHashSet<>();
        setTaskEndTime(taskStartTime.plus(taskDuration));
    }

    public Epic(
            Integer taskKey, String taskTitle, String taskDescription, TaskStatus taskStatus, Instant taskStartTime,
            Duration taskDuration, Instant tmpTimeTaskWasUpdated, Set<SubTask> relatedSubtasks
    ) {
        super(taskKey, taskTitle, taskDescription, taskStatus, taskStartTime, taskDuration, tmpTimeTaskWasUpdated);
        this.relatedSubtasks = relatedSubtasks;
        setTaskEndTime(taskStartTime.plus(taskDuration));
    }

    public Set<SubTask> getRelatedSubTasks() {
        return relatedSubtasks;
    }

    public void addChildSubTask(SubTask subTask) {
        relatedSubtasks.add(subTask);
    }

    public void removeChildSubTask(SubTask subTask) {
        relatedSubtasks.remove(subTask);
    }

    @Override
    public String toString() {
        return "Epic;"
                + getTaskKey() + ";"
                + getRelatedSubTasks() + ";"
                + getTaskTitle() + ";"
                + getTaskDescription() + ";"
                + getTaskStatus() + ";"
                + getTaskStartTime() + ";"
                + getTaskDuration() + ";"
                + getTaskTimeUpdateCheck();
    }
}
