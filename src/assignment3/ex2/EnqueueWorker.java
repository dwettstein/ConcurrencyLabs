package assignment3.ex2;

public class EnqueueWorker extends QueueWorker {

	public EnqueueWorker(IQueue queue) {
		super(queue);
	}

	@Override
	protected void work() {
		this.queue.enq(this.queueUpdates.get());
	}

}
