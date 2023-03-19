package controls;

import tasks.Task;

class Node {

    public Node next;
    public Node prev;
    public Task task;

    public Node(Task task, Node next, Node prev) {
        this.next = next;
        this.prev = prev;
        this.task = task;
    }
}
