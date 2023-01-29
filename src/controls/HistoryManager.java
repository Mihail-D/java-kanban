package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void addHistory(Task task);

    void removeHistoryRecord(String taskId);

    List<Task> getHistory();

    void clearHistoryStorage();
}