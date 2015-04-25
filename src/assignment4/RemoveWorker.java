package assignment4;

public class RemoveWorker extends ListWorker {

	public RemoveWorker(ISet list, int[] numbersToRemove) {
		super(list, numbersToRemove);
	}

	@Override
	protected void work() {
		this.list.remove(this.numbersForWork[this.numberOfUpdates.get()]);
	}

}
