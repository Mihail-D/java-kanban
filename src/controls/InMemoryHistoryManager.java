package controls;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> historyStorage = new ArrayList<>();

    @Override
    public void add(Task task) {
        task.setViewed();
        fillHistoryStorage(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyStorage;
    }


    public void fillHistoryStorage(Task task) {
        if (historyStorage.size() == 10) {
            historyStorage.remove(0);
        }
        historyStorage.add(task);
    }
}
