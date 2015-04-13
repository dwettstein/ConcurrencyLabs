package assignment3.ex2;

public abstract class QueueWorker extends Thread {
	private static final int MAX_UPDATES = 100000;
	protected ThreadLocal<Integer> queueUpdates; 
	protected IQueue queue;
	
	public QueueWorker(IQueue queue) {
		this.queueUpdates = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 0;
			}
		};
		this.queue = queue;
	}
	
	protected abstract void work();
	
	@Override
	public void run() {
		while (true) {
			int currentUpdates = this.queueUpdates.get();
			if (currentUpdates < MAX_UPDATES) {
				this.work();
				currentUpdates++;
				this.queueUpdates.set(currentUpdates);
			}
			else {
				break;
			}
		}
	}
}
