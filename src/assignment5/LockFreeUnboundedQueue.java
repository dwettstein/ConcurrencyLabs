package assignment5;

public class LockFreeUnboundedQueue extends AbstractUnboundedQueue {
	
	public LockFreeUnboundedQueue() {
		super();
	}
	
	@Override
	public void enq(Object item) {
		Node newNode = new Node(item);
		while (true) {
			Node last = this.tail;
			Node next = last.next;
			if (last == this.tail) {
				if (next == null) {
					if (last.next.compareAndSet(next, newNode)) {
						this.tail.compareAndSet(last, newNode);
						return;
					}
				} else {
					this.tail.compareAndSet(last, next);
				}
			}
		}
	}
	
	@Override
	public Object deq() {
		while (true) {
			Node first = this.head;
			Node last = this.tail;
			Node next = first.next;
			
			if (first == this.head) {
				if (next == null) {
					System.out.println("The queue is empty.");
					//throw new Exception();
					return null;
				}
				this.tail.compareAndSet(last, next);
			}
			else {
				Object result = next.object;
				if (this.head.compareAndSet(first, next)) {
					return result;
				}
			}
		}
	}
	
}
