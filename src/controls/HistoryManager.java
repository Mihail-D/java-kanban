package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void taskAdd();

    List<Task> getHistory();
}
