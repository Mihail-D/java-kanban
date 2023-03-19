package controls;

import tasks.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    private Node linkLast(Task task) {
        Node node = new Node(task, null, tail);
        if (head == null) {
            head = node;
        }
        else {
            tail.next = node;
        }
        tail = node;
        return node;
    }

    private void removeNode(Node node) {
        Node nextNode = node.next;
        Node prevNode = node.prev;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        }
        else if (prevNode == null) {
            nextNode.prev = null;
            head = nextNode;
        }
        else if (nextNode == null) {
            prevNode.next = null;
            tail = prevNode;
        }
        else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getTaskKey())) {
            remove(task.getTaskKey());
        }
        history.put(task.getTaskKey(), linkLast(task));
    }

    @Override
    public void remove(int taskKey) {
        if (history.containsKey(taskKey)) {
            removeNode(history.get(taskKey));
            history.remove(taskKey);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskHistory = new ArrayList<>();
        Node node = head;
        while (node != null) {
            taskHistory.add(node.task);
            node = node.next;
        }
        return taskHistory;
    }
}
