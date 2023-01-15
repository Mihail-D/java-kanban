package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addHistory(Task task);

    void removeHistory(String taskId);

    List<Task> getHistory();

    void clearHistoryStorage();
}