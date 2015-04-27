package assignment4;

public class OptimisticFineGrainedLockList extends AbstractFineGrainedLockList {
	
	public OptimisticFineGrainedLockList() {
		super();
	}
	
	@Override
	public boolean add(int object) {
		int key = object;
		
		while (true) {
			Node pred = this.head;
			Node succ = pred.next;
			
			while (succ.getKey() <= key) {
				// The object is not found yet, make a hand-over-hand of nodes to the successor of the current node.
				pred = succ;
				succ = succ.next;
			}
				
			try {
				// Lock current and successor node.
				pred.lock();
				succ.lock();
				
				if (validate(pred, succ)) {
//					if (pred.getKey() == key || succ.getKey() == key) {
//						// Don't add nodes with equal keys.
//						return false;
//					}
					
					// We have found the correct place, now insert the node.
					Node insertNode = new Node(object);
					pred.setNextNode(insertNode);
					insertNode.setNextNode(succ);
					return true;
				}
			} 
			finally {
					succ.unlock();
					pred.unlock();
			}
		}
	}
	
	@Override
	public boolean remove(int object) {
		int key = object;
		boolean isObjectFound = false;
		
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			
			// Traverse the list.
			while (curr.getKey() <= key) {
				if (object == curr.getKey()) {
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
					if (curr.getKey() == key) {
						// We have found and validated the object, now set the new links.
						pred.setNextNode(curr.next);
						return true;
					} 
					else {
						return false;
					}
				}
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}
	
	@Override
	public boolean contains(int object) {
		int key = object;
		boolean isObjectFound = false;
		
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			
			// Traverse the list.
			while (curr.getKey() <= key) {
				if (object == curr.getKey()) {
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
			
			// If the object has been found, lock and re-check if it is really available.
			try {
				// Lock current and predecessor node.
				pred.lock();
				curr.lock();
				
				if (validate(pred, curr)) {
					if (curr.getKey() == key) {
						// We have found and validated the object.
						return true;
					} 
					else {
						return false;
					}
				}
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}
	
	private boolean validate(Node pred, Node curr) {
		Node node = this.head;
		while (node.getKey() <= pred.getKey()) {
			// Check if node "pred" is still accessible.
			if (node == pred) {
				// Check if the provided nodes are still adjacent.
				return pred.next == curr;
			}
			node = node.next;
		}
		return false;
	}
	
}
