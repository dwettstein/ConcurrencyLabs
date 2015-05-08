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
			Node last = this.tail.get();
			Node next = last.next.get();
			
			Node newNode = new Node(value);
			// INFO:
			// I'm using also CAS here in order to implement the Node class only once for both queue types.
			// Usually CAS is not needed here!
			last.next.compareAndSet(next, newNode); 
			this.tail.compareAndSet(last, newNode);
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
			Node first = this.head.get();
			Node next = first.next.get();
			
			// Check if queue is empty.
			if (next == null || next.next.get() == null) {
				//System.out.println("The queue is empty.");
				//throw new Exception();
				return null;
			}
			// Get the object at the head of the queue.
			result = next.object;
			// INFO:
			// I'm using also CAS here in order to implement the Node class only once for both queue types.
			// Usually CAS is not needed here!
			this.head.compareAndSet(first, next.next.get());
		}
		finally {
			this.deqLock.unlock();
		}
		return result;
	}
	
}