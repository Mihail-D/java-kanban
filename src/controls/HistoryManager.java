package controls;

import tasks.Task;

public interface HistoryManager {

    void add(Task task);

    void getHistory();

    void removeNode(Node<Task> node);

    //void fillHistoryStorage(Task task);
}
