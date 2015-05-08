package assignment5;

public class DequeueWorker extends AbstractWorker {

	public DequeueWorker(IQueue queue, int[] numbersToRemove) {
		super(queue, numbersToRemove);
	}

	@Override
	protected void work() {
		this.queue.deq();
//		Object item = this.queue.deq();
//		System.out.println("Removed item " + item);
//		System.out.println(this.queue.toString());
	}

}
