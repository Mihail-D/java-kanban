package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(String taskId);

    List<Task> getHistory();


}
