package controls;

public class Node<T> {
    T node;
    Node<T> nextNode;
    Node<T> previousNode;

    public Node(Node<T> previousNode, T node) {
        this.previousNode = previousNode;
        this.node = node;
        this.nextNode = null;
    }

    public T getNode() {
        return this.node;
    }

    public void setNode(T node) {
        this.node = node;
    }
}
