package assignment5;

public class DequeueWorker extends AbstractWorker {

	public DequeueWorker(IQueue queue, int[] numbersToRemove) {
		super(queue, numbersToRemove);
	}

	@Override
	protected void work() {
		Object item = this.queue.deq();
	}

}
