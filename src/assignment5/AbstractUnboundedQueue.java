package assignment5;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractUnboundedQueue implements IQueue {

	protected AtomicReference<Node> head;
	protected AtomicReference<Node> tail;
	protected final Node sentinelNode;

	public AbstractUnboundedQueue() {
		sentinelNode = new Node(null);
		this.head = new AtomicReference<Node>(sentinelNode);
		this.tail = new AtomicReference<Node>(sentinelNode);
	}

	public abstract void enq(Object item);
	
	public abstract Object deq();
	
	public String toString() {
		String result = "";
		Node currentNode = this.head.get();
		result += currentNode.key;
		while (currentNode != null && currentNode.hasNext()) {
			currentNode = currentNode.next.get();
			result += " -> " + currentNode.key;
		}
		return result;
	}

	@Override
	public int getSize() {
		Node currentNode = this.head.get();
		int result = 0;
		while (currentNode != null && currentNode.hasNext()) {
			currentNode = currentNode.next.get();
			result++;
		}
		result = result - 1; // Subtract one because of sentinel node at the end of the list.
		return result;
	}

}