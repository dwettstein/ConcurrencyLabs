package assignment4;

public abstract class ListWorker extends Thread {
	private int maxUpdates = 100;
	protected ThreadLocal<Integer> numberOfUpdates; 
	protected ISet list;
	
	public ListWorker(ISet list, int maxUpdates) {
		this.numberOfUpdates = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 0;
			}
		};
		this.list = list;
		this.maxUpdates = maxUpdates;
	}
	
	protected abstract void work();
	
	@Override
	public void run() {
		while (true) {
			int currentUpdates = this.numberOfUpdates.get();
			if (currentUpdates < this.maxUpdates) {
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
