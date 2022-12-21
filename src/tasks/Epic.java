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

    @Override
    public String toString() {
        return "Epic { RelatedSubTask='" + relatedSubTask + '\'' +
                super.toString() + '\'' +
                '}';
    }
}

/* // TODO
У класса может быть некоторое количество конструкторов, в данном случае можно
задать конструктор без мапы сабтасков в дополнение к существующему.
*/