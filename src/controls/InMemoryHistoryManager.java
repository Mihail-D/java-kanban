package controls;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public static List<Task> historyStorage = new ArrayList<>();

    @Override
    public void add(Task task) {
        task.setViewed();
    }
    @Override
    public List<Task> getHistory() {
        return historyStorage;
    }
}
