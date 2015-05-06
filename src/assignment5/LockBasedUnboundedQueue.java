package assignment5;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedUnboundedQueue extends AbstractUnboundedQueue {
	
	ReentrantLock enqLock;
	ReentrantLock deqLock;
	
	public LockBasedUnboundedQueue() {
		super();
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
	}
	
	@Override
	public void enq(Object value) {
		this.enqLock.lock();
		try {
			Node newNode = new Node(value);
			AtomicReference<Node> newNodeRef = new AtomicReference<Node>(newNode);
			this.tail.get().setNextNode(newNodeRef);
			this.tail = newNodeRef;
		}
		finally {
			this.enqLock.unlock();
		}
	}
	
	@Override
	public Object deq() {
		Object result = null;
		this.deqLock.lock();
		try {
			// Check if queue is empty.
			if (this.head.get().next == this.tail) {
				//System.out.println("The queue is empty.");
				//throw new Exception();
				return null;
			}
			// Get the object at the head of the queue.
			Node resultNode = this.head.get().next.get();
			if (resultNode != null) {
				result = resultNode.object;
				this.head.get().next = resultNode.next;
			}
		}
		finally {
			this.deqLock.unlock();
		}
		return result;
	}
	
}