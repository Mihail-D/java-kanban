package controls;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

    void fillHistoryStorage(Task task);

    void remove(int id);
}
