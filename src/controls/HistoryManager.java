package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int taskKey);

    List<Task> getHistory();
}
