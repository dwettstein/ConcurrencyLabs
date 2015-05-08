package assignment5;

public class LockFreeUnboundedQueue extends AbstractUnboundedQueue {
	
	public LockFreeUnboundedQueue() {
		super();
	}
	
	@Override
	public void enq(Object item) {
		Node newNode = new Node(item);
		while (true) {
			Node last = this.tail.get();
			Node next = last.next.get();
			
			if (last == this.tail.get()) {
				if (next == null) {
					if (last.next.compareAndSet(next, newNode)) {
						this.tail.compareAndSet(last, newNode);
						return;
					}
				}
				else {
					this.tail.compareAndSet(last, next);
				}
			}
		}
	}
	
	@Override
	public Object deq() {
		while (true) {
			Node first = this.head.get();
			Node last = this.tail.get();
			Node next = (first == null) ? null : first.next.get();
			
			if (first == this.head.get()) {
				if (first == last) {
					if (next == null) {
						//System.out.println("The queue is empty.");
						//throw new Exception("The queue is empty.");
						return null;
					}
					this.tail.compareAndSet(last, next);
				}
				else {
					Object value = (next == null) ? null : next.object;
					if (this.head.compareAndSet(first, next)) {
						return value;
					}
				}
			}
		}
	}
	
}
