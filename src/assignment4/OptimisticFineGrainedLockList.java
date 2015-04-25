package assignment4;

public class OptimisticFineGrainedLockList implements ISet {
	Node head;
	Node tail;
	Node headSentinelNode;
	Node tailSentinelNode;
	
	public OptimisticFineGrainedLockList() {
		headSentinelNode = new Node(Integer.MIN_VALUE);
		this.head = headSentinelNode;
		tailSentinelNode = new Node(Integer.MAX_VALUE);
		this.tail = tailSentinelNode;
		this.head.setNextNode(tailSentinelNode);
	}
	
	public boolean add(Object object) {
		int key = object.hashCode();
		Node pred = this.head;
		Node succ = pred.next;
		boolean isSuccess = false;

		try {
			// Lock current and successor node.
			pred.lock();
			succ.lock();

			// Traverse the list and find the correct place to insert the node.
			if (pred == this.headSentinelNode && succ == this.tailSentinelNode) {
				// The list is empty and we can directly insert the node.
			}
			else {
				while (succ.key <= key) {
					// The object is not found yet, make a hand-over-hand of nodes and lock the successor of the current node.
					pred.unlock();
					pred = succ;
					succ = succ.next;
					succ.lock();
				}
			}
			
			// We have found the correct place, now insert the node.
			Node insertNode = new Node(object);
			pred.setNextNode(insertNode);
			insertNode.setNextNode(succ);
			isSuccess = true;
		} finally {
			succ.unlock();
			pred.unlock();
		}
		return isSuccess;
	}

	public boolean remove(Object object) {
		int key = object.hashCode();
		boolean isObjectFound = false;
		
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			
			// Check if list is empty.
			if (pred == this.headSentinelNode && curr == this.tailSentinelNode) {
				return false;
			}
			
			// Traverse the list.
			while (curr.key <= key) {
				if (object == curr.object) {
					// We have found the object.
					isObjectFound = true;
					break;
				}
				// The object is not found yet, make a hand-over-hand of nodes and lock the successor of the current node.
				pred = curr;
				curr = curr.next;
			}
			
			if (!isObjectFound) {
				// The object has not been found in the list.
				return false;
			}
			
			try {
				// Lock current and predecessor node.
				pred.lock();
				curr.lock();
				
				if (validate(pred, curr)) {
					if (curr.object == object) {
						// We have found and validated the object, now set the new links.
						pred.next = curr.next;
						return true;
					} else
						return false;
				}
			} finally {
				pred.unlock();
				curr.unlock();
			}
		}
	}
	
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
	
	private boolean validate(Node pred, Node curr) {
		Node node = this.head;
		while (node.key <= pred.key) {
			if (node == pred)
				return pred.next == curr;
			node = node.next;
		}
		return false;
	}
	
	public String toString() {
		String result = "";
		Node currentNode = this.head;
		result += currentNode.key;
		while (currentNode.hasNext()) {
			currentNode = currentNode.next;
			result += " - " + currentNode.key;
		}
		return result;
	}
	
}
