package controls;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public static CustomLinkedList historyStorage = new CustomLinkedList();
    public static Map<String, Node> historyRegister = new HashMap<>();
    public static List<Task> historyReport = new ArrayList<>();

    @Override
    public void addHistory(Task task) {
        historyStorage.linkLast(task);
    }

    @Override
    public void removeHistory(String taskId) {
        historyStorage.removeNode(historyStorage.getNode(taskId));
    }

    public void clearHistoryStorage() {
        for (String i : historyRegister.keySet()) {
            historyStorage.removeNode(historyStorage.getNode(i));
        }
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
                System.out.println(task.getTaskId()); // TODO
                System.out.println(historyRegister); // TODO

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
            historyStorage.size++;
            historyRegister.put(task.getTaskId(), element);

        }

        void removeNode(Node node) {
            if (node != null) {
                historyRegister.remove(node.getTask().getTaskId());
                historyReport.remove(node.getTask());
                System.out.println(node.getTask().getTaskId()); // TODO

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
            historyStorage.size--;
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

    }
}
