package assignment3.ex2;

public class TwoLockQueue extends NoLockQueue {
	private CCASLock headLock;
	private CCASLock tailLock;
	
	public TwoLockQueue(int queueSize) {
		super(queueSize);
		headLock = new CCASLock();
		tailLock = new CCASLock();
	}
	
	@Override
	public void enq(int intItem) {
		while (true) {
			// We will update the tail.
			this.tailLock.lock();
			if (tail - head == queueSize) {
				// The queue is full, do nothing and release the lock.
				this.tailLock.unlock();
			}
			else {
				// We have the lock and the queue is not full, continue.
				break;
			}
		}
		items[tail % queueSize] = intItem;
		tail++;
		//System.out.println(this.toString());
		this.tailLock.unlock();
	};
	
	@Override
	public int deq() {
		// We will update the head.
		while (true) {
			this.headLock.lock();
			if (tail == head) {
				// The queue is empty, do nothing and release the lock.
				this.headLock.unlock();
			}
			else {
				// We have the lock and the queue is not empty, continue.
				break;
			}
		}
		int item = items[head % queueSize];
		head++;
		//System.out.println(this.toString());
		this.headLock.unlock();
		return item;
	}
	
}
