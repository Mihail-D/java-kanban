package controls;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> tasksHistory = new CustomLinkedList<>();

    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTasks();
    }

    @Override
    public void add(Task task, int index) {
        if (index == 0) {
            tasksHistory.linkLast(task, task.getTaskKey());
        }
        else if (index == 1) {
            tasksHistory.linkFirst(task, task.getTaskKey());
        }
    }

    @Override
    public void removeHistory(int taskKey) {
        tasksHistory.removeNode(taskKey);
    }

    public static class CustomLinkedList<T> {

        private Node<T> head;
        private Node<T> tail;
        private int size;
        public final HashMap<Integer, Node<T>> tasksHistoryOrder = new HashMap<>();

        public void removeNode(int index) {
            if (!tasksHistoryOrder.isEmpty()) {
                Node<T> nodeX = tasksHistoryOrder.get(index);
                if (nodeX.previousNode == null) {
                    head = nodeX.nextNode;
                }
                else {
                    nodeX.previousNode.nextNode = nodeX.nextNode;
                }
                if (nodeX.nextNode == null) {
                    tail = nodeX.previousNode;
                }
                else {
                    nodeX.nextNode.previousNode = nodeX.previousNode;
                }
                tasksHistoryOrder.remove(index);
                int tmpSize = this.size;
                size--;
            }
        }

        public void linkFirst(T item, Integer taskKey) {
            this.head = new Node<>(this.head, item);
            tasksHistoryOrder.put(taskKey, this.head);
            this.size++;
        }

        public void linkLast(T item, Integer taskKey) {
            if (this.size == 10) {
                for (Integer historyId : tasksHistoryOrder.keySet()) {
                    if (tasksHistoryOrder.get(historyId).equals(tail)) {
                        removeNode(historyId);
                    }
                }
            }
            if (tasksHistoryOrder.containsKey(taskKey)) {
                removeNode(taskKey);
            }
            Node<T> newNode = new Node<>(tail, item);
            tasksHistoryOrder.put(taskKey, newNode);
            if (tail != null) {
                this.tail.nextNode = newNode;
            }
            else {
                head = newNode;
            }
            tail = newNode;
            size++;
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> historyList = new ArrayList<>(this.size);
            Node<T> nodeX = head;
            while (nodeX != null) {
                historyList.add(nodeX.node);
                nodeX = nodeX.nextNode;
            }
            return historyList;
        }

        public int size() {
            return tasksHistoryOrder.size();
        }

        public boolean isEmpty() {
            return this.size == 0;
        }

    }

}
