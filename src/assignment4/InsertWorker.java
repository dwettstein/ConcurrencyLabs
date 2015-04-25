package assignment4;

public class InsertWorker extends ListWorker {

	public InsertWorker(ISet list, int[] numbersToInsert) {
		super(list, numbersToInsert);
	}

	@Override
	protected void work() {
		this.list.add(this.numbersForWork[this.numberOfUpdates.get()]);
	}

}
