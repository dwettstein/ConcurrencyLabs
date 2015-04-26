package assignment4;

public abstract class AbstractFineGrainedLockList implements ISet {

	protected volatile Node head;
	protected volatile Node tail;
	protected Node headSentinelNode;
	protected Node tailSentinelNode;

	public AbstractFineGrainedLockList() {
		headSentinelNode = new Node(Integer.MIN_VALUE);
		this.head = headSentinelNode;
		tailSentinelNode = new Node(Integer.MAX_VALUE);
		this.tail = tailSentinelNode;
		this.head.setNextNode(tailSentinelNode);
	}

	public abstract boolean add(Object object);
	
	public abstract boolean remove(Object object);
	
	public boolean contains(Object object) {
		int key = object.hashCode();
		Node pred = this.head;
		Node curr = pred.next;
		boolean isFound = false;
		
		try {
			// Lock current and predecessor node.
			pred.lock();
			curr.lock();

			// Traverse the list.
			while (curr.key <= key) {
				if (object == curr.object) {
					// We have found the object.
					isFound = true;
					break;
				}
				// The object is not found yet, make a hand-over-hand of nodes and lock the successor of the current node.
				pred.unlock();
				pred = curr;
				curr = curr.next;
				curr.lock();
			}
		} finally {
			curr.unlock();
			pred.unlock();
		}
		return isFound;
	}
	
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