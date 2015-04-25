package assignment4;

public abstract class ListWorker extends Thread {
	protected int[] numbersForWork;
	protected ThreadLocal<Integer> numberOfUpdates; 
	protected ISet list;
	
	public ListWorker(ISet list, int[] numbersForWork) {
		this.numberOfUpdates = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 0;
			}
		};
		this.list = list;
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
