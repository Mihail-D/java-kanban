package controls;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public static CustomLinkedList historyStorage = new CustomLinkedList();
    public static Map<String, Node> historyRegister = new LinkedHashMap<>();
    public static List<Task> historyReport = new ArrayList<>();

    @Override
    public void addHistory(Task task) {
        historyStorage.linkLast(task);
    }

    @Override
    public void removeHistoryRecord(String taskId) {
        Node node = historyStorage.getNode(taskId);
        if (node != null) {
            historyStorage.removeNode(node);
            historyReport.removeIf(i -> i.equals(node.getTask()));
            historyRegister.remove(node.getTask().getTaskId());
        }
    }

    @Override
    public void clearHistoryStorage() {
        for (String i : historyRegister.keySet()) {
            historyStorage.removeNode(historyStorage.getNode(i));
        }
        historyRegister.clear();
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

        public void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);
            System.out.println(task.getTaskId()); // TODO

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
            size++;
            historyRegister.put(task.getTaskId(), element);

        }

        public void removeNode(Node node) {
            if (node != null) {
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
            size--;
        }

        public List<Task> getTasks() {

            Node element = head;
            while (element != null) {
                if (!historyReport.contains(element.getTask())) {
                    historyReport.add(element.getTask());
                }
                element = element.getNext();
            }

            return historyReport;
        }

        Node getNode(String taskId) {
            return historyRegister.get(taskId);
        }
    }
}
