package controls;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public static CustomLinkedList historyStorage = new CustomLinkedList();
    public static Map<String, Node> historyRegister = new HashMap<>();
    public static List<Task> historyReport = new ArrayList<>();

    @Override
    public void add(Task task) {
        historyStorage.linkLast(task);
        historyStorage.size++;
    }

    @Override
    public void remove(String taskId) {
        historyStorage.removeNode(historyStorage.getNode(taskId));
        historyStorage.size--;
    }

    @Override
    public List<Task> getHistory() {
        return historyStorage.getTasks();
    }

    public static class CustomLinkedList {

        private Node head;
        private Node tail;
        private int size;

        public int getSize() {
            return size;
        }

        void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);

            if (historyRegister.containsKey(task.getTaskId())) {
                removeNode(historyRegister.get(task.getTaskId()));
            }

            if (head == null) {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            }
            else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            historyRegister.put(task.getTaskId(), element);
        }

        void removeNode(Node node) {
            if (node != null) {
                historyRegister.remove(node.getTask().getTaskId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        List<Task> getTasks() {
            Node element = head;
            while (element != null) {
                historyReport.add(element.getTask());
                element = element.getNext();
            }
            return historyReport;
        }

        Node getNode(String taskId) {
            return historyRegister.get(taskId);
        }

        @Override                                              // TODO
        public String toString() {
            return "CustomLinkedList{" +
                    "head=" + head +
                    ", tail=" + tail +
                    ", size=" + size +
                    '}';
        }
    }
}
