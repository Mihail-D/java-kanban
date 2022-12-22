package controls;

import tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    @Override
    public void add(Task task) {
        task.setViewed();
    }
    @Override
    public List<Task> getHistory() {
        return InMemoryTaskManager.historyStorage;
    }
}
