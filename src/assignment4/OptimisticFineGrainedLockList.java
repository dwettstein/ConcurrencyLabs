package assignment4;

public class OptimisticFineGrainedLockList extends AbstractFineGrainedLockList {
	
	public OptimisticFineGrainedLockList() {
		super();
	}
	
	@Override
	public boolean add(Object object) {
		int key = object.hashCode();

		while (true) {
			Node pred = this.head;
			Node succ = pred.next;		
			
			// Traverse the list and find the correct place to insert the node.
			if (pred == this.headSentinelNode && succ == this.tailSentinelNode) {
				// The list is empty and we can directly insert the node.
			}
			else {
				while (succ != null && succ.key <= key) {
					// The object is not found yet, make a hand-over-hand of nodes to the successor of the current node.
					pred = succ;
					succ = succ.next;
				}
			}
			
			if (pred != null || succ != null) {
			try {
				// TODO: Should be removed.
				if (pred == null || succ == null) {
					return false;
				}
				
				// Lock current and successor node.
				pred.lock();
				succ.lock();
				
				if (validate(pred, succ)) {
					// We have found the correct place, now insert the node.
					Node insertNode = new Node(object);
					pred.setNextNode(insertNode);
					insertNode.setNextNode(succ);
					return true;
				}
				else {
					return false;
				}
			} 
//			catch (Exception e) {
//				System.out.println("Catched exception: " + e.toString());
//			}
			finally {
//				try {
					succ.unlock();
					pred.unlock();
//				}
//				catch (Exception e) {
//					System.out.println("Catched exception: " + e.toString());
//				}
			}
			}
		}
		
	}
	
	@Override
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
			while (curr != null && curr.key <= key) {
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
//				if (pred == null || curr == null) {
//					return false;
//				}
				
				// Lock current and predecessor node.
				pred.lock();
				curr.lock();
				
				if (validate(pred, curr)) {
					if (curr.object == object) {
						// We have found and validated the object, now set the new links.
						pred.setNextNode(curr.next);
						return true;
					} 
					else {
						return false;
					}
				}
				else {
					return false;
				}
			} finally {
				curr.unlock();
				pred.unlock();
			}
		}
	}
	
	private boolean validate(Node pred, Node curr) {
		try {
			Node node = this.head;
			while (node.key <= pred.key) {
				// Check if node "pred" is still accessible.
				if (node == pred)
					// Check if the provided nodes are still adjacent.
					return pred.next == curr;
				node = node.next;
			}
		}
		catch (Exception e) {
			//System.out.println("Catched exception: " + e.toString());
			return false;
		}
		return false;
	}
	
}
