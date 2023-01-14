package controls;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public static CustomLinkedList<Task> historyStorage = new CustomLinkedList<>();
    public static Map<String, Node<Task>> historyRegister = new HashMap<>();
    public static List<Task> historyReport = new ArrayList<>();

    @Override
    public void add(Task task) {
        //removeNode();
        task.setViewed();
        historyStorage.addLast(task);
        historyRegister.put(task.getTaskId(), historyStorage.getLast());
        System.out.println("historyStorage size from fillHistoryStorage() " + historyStorage.size()); // TODO

    }

    @Override
    public void getHistory() {
        Node<Task> current = historyStorage.head;
        if (historyStorage.head == null) {
            System.out.println("Список пуст.");
            return;
        }
        while (current != null) {
            historyReport.add(current.data);
            current = current.next;
        }
    }

    @Override
    public void removeNode(Node<Task> node) {
        Node<Task> current = historyStorage.head;
        if (historyStorage.head == null) {
            System.out.println("Список пуст.");
            return;
        }
        while (current != null) {
            historyReport.add(current.data);
            current = current.next;
        }
    }

// ************************************************************************************

    public static class CustomLinkedList<T> {

        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        public void addLast(T element) {

            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            }
            else {
                oldTail.next = newNode;
            }
            size++;
        }

        public Node<T> getLast() {

            final Node<T> currentTail = tail;
            if (currentTail == null) {
                throw new NoSuchElementException();
            }
            return tail;
        }

        public void removeHistoryRecord(Node<Task> node) {
            Node<Task> predecessor = node.prev;
            Node<Task> successor = node.next;

            predecessor.setNext(successor);
            successor.setPrev(predecessor);
        }

        public int size() {
            return this.size;
        }
    }
}
