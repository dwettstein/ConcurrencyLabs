package assignment4;

public class FineGrainedLockList extends AbstractFineGrainedLockList {
	
	public FineGrainedLockList() {
		super();
	}
	
	@Override
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
	
	@Override
	public boolean remove(Object object) {
		int key = object.hashCode();
		Node pred = this.head;
		Node curr = pred.next;
		
		// Check if list is empty.
		if (pred == this.headSentinelNode && curr == this.tailSentinelNode) {
			return false;
		}
		
		try {
			// Lock current and predecessor node.
			pred.lock();
			curr.lock();
			
			// Traverse the list.
			while (curr.key <= key) {
				if (object == curr.object) {
					// We have found the object, now set the new links.
					pred.next = curr.next;
					return true;
				}
				// The object is not found yet, make a hand-over-hand of nodes and lock the successor of the current node.
				pred.unlock();
				pred = curr;
				curr = curr.next;
				curr.lock();
			}
			// The object has not been found in the list.
			return false;
		} finally {
			curr.unlock();
			pred.unlock();
		}
	}
	
}