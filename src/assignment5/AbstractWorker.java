package assignment5;

public abstract class AbstractWorker extends Thread {
	protected int[] numbersForWork;
	protected ThreadLocal<Integer> numberOfUpdates; 
	protected IQueue queue;
	
	public AbstractWorker(IQueue queue, int[] numbersForWork) {
		this.numberOfUpdates = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 0;
			}
		};
		this.queue = queue;
		this.numbersForWork = numbersForWork;
	}
	
	protected abstract void work();
	
	@Override
	public void run() {
		while (true) {
			int currentUpdates = this.numberOfUpdates.get();
			if (currentUpdates < this.numbersForWork.length) {
				this.work();
				currentUpdates++;
				this.numberOfUpdates.set(currentUpdates);
			}
			else {
				break;
			}
		}
	}
}
