package assignment3.ex2;

public class DequeueWorker extends QueueWorker {

	public DequeueWorker(IQueue queue) {
		super(queue);
	}

	@Override
	protected void work() {
		this.queue.deq();
	}

}
