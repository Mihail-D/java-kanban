package tasks;

public class Task extends AbstractTask {
    public Task(String taskTitle, String taskDescription, String taskId, TaskStages taskStatus) {
        super(taskTitle, taskDescription, taskId, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" + super.toString();
    }
}
