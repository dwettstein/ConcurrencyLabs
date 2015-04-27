package assignment4;

public class FineGrainedLockList extends AbstractFineGrainedLockList {
	
	public FineGrainedLockList() {
		super();
	}
	
	@Override
	public boolean add(int object) {
		int key = object;
		Node pred = this.head;
		Node succ = pred.next;

		try {
			// Lock current and successor node.
			pred.lock();
			succ.lock();

			while (succ.getKey() <= key) {
				// The object is not found yet, make a hand-over-hand of nodes and lock the successor of the current node.
				pred.unlock();
				pred = succ;
				succ = succ.next;
				succ.lock();
			}
			
//			if (pred.getKey() == key || succ.getKey() == key) {
//				// Don't add nodes with equal keys.
//				return false;
//			}
			
			// We have found the correct place, now insert the node.
			Node insertNode = new Node(object);
			pred.setNextNode(insertNode);
			insertNode.setNextNode(succ);
			return true;
		} finally {
			succ.unlock();
			pred.unlock();
		}
	}
	
	@Override
	public boolean remove(int object) {
		int key = object;
		Node pred = this.head;
		Node curr = pred.next;
		
		try {
			// Lock current and predecessor node.
			pred.lock();
			curr.lock();
			
			// Traverse the list.
			while (curr.getKey() <= key) {
				if (object == curr.getKey()) {
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
	
	@Override
	public boolean contains(int object) {
		int key = object;
		Node pred = this.head;
		Node curr = pred.next;

		try {
			// Lock current and predecessor node.
			pred.lock();
			curr.lock();

			// Traverse the list.
			while (curr.getKey() <= key) {
				if (key == curr.getKey()) {
					// We have found the object.
					return true;
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
		return false;
	}
	
}