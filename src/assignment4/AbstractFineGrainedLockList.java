package assignment4;

public abstract class AbstractFineGrainedLockList implements ISet {

	protected Node head;
	protected Node tail;
	protected Node headSentinelNode;
	protected Node tailSentinelNode;

	public AbstractFineGrainedLockList() {
		headSentinelNode = new Node(Integer.MIN_VALUE);
		this.head = headSentinelNode;
		tailSentinelNode = new Node(Integer.MAX_VALUE);
		this.tail = tailSentinelNode;
		this.head.setNextNode(this.tail);
	}

	public abstract boolean add(int object);
	
	public abstract boolean remove(int object);
	
	public abstract boolean contains(int object);
	
	public String toString() {
		String result = "";
		Node currentNode = this.head;
		result += currentNode.key;
		while (currentNode.hasNext()) {
			currentNode = currentNode.next;
			result += " -> " + currentNode.key;
		}
		return result;
	}

	@Override
	public int getSize() {
		Node currentNode = this.head;
		int result = 0;
		while (currentNode.hasNext()) {
			currentNode = currentNode.next;
			result++;
		}
		result = result - 1; // Subtract one because of sentinel node at the end of the list.
		return result;
	}

}