package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void add(Task task, int index);

    void removeHistory(int taskKey);
}