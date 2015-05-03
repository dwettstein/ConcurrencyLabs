package assignment5;

public class EnqueueWorker extends AbstractWorker {

	public EnqueueWorker(IQueue queue, int[] numbersToInsert) {
		super(queue, numbersToInsert);
	}

	@Override
	protected void work() {
		this.queue.enq(this.numbersForWork[this.numberOfUpdates.get()]);
	}

}
