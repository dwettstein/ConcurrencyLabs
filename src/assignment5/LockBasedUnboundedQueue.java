package assignment5;

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
			this.tail.setNextNode(newNode);
			tail = newNode;
		}
		finally {
			this.enqLock.unlock();
		}
	}
	
	@Override
	public Object deq() {
		Object result;
		this.deqLock.lock();
		try {
			// Check if queue is empty.
			if (this.head.next == this.tail) {
				System.out.println("The queue is empty.");
				//throw new Exception();
				return null;
			}
			// Get the object at the head of the queue.
			result = this.head.next.object;
			this.head = this.head.next; // TODO: Does this work with the sentinel node
		}
		finally {
			this.deqLock.unlock();
		}
		return result;
	}
	
}