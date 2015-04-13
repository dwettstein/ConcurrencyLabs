package assignment3.ex2;

public class SingleLockQueue extends NoLockQueue {
	private CCASLock lock;
	
	public SingleLockQueue(int queueSize) {
		super(queueSize);
		this.lock = new CCASLock();
	}
	
	@Override
	public void enq(int intItem) {
		while (true) {
			this.lock.lock();
			if (tail - head == queueSize) {
				// The queue is full, do nothing and release the lock.
				this.lock.unlock();
			}
			else {
				// We have the lock and the queue is not full, continue.
				break;
			}
		}
		items[tail % queueSize] = intItem;
		tail++;
		//System.out.println(this.toString());
		this.lock.unlock();
	};
	
	@Override
	public int deq() {
		while (true) {
			this.lock.lock();
			if (tail == head) {
				// The queue is empty, do nothing and release the lock.
				this.lock.unlock();
			}
			else {
				// We have the lock and the queue is not empty, continue.
				break;
			}
		}
		int item = items[head % queueSize];
		head++;
		//System.out.println(this.toString());
		this.lock.unlock();
		return item;
	}
}
