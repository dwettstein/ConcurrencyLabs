package assignment3.ex2;

public class NoLockQueue implements IQueue {
	
	protected int queueSize;
	protected volatile int head;
	protected volatile int tail;
	protected int items[];
	
	public NoLockQueue(int queueSize) {
		this.queueSize = queueSize;
		this.head = 0;
		this.tail = 0;
		items = new int[queueSize];
	}
	
	@Override
	public void enq(int intItem) {
		while (tail - head == queueSize) {
			// The queue is full, do nothing.
		}
		items[tail % queueSize] = intItem;
		tail++;
		//System.out.println(this.toString());
	};
	
	@Override
	public int deq() {
		while (tail == head) {
			// The queue is empty, do nothing.
		}
		int item = items[head % queueSize];
		head++;
		//System.out.println(this.toString());
		return item;
	}
	
	@Override
	public String toString() {
		return "Head: '" + this.head + "', tail: '" + this.tail + "'.";
	}
}
