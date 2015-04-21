package assignment4;

public class InsertWorker extends ListWorker {

	public InsertWorker(ISet list, int maxUpdates) {
		super(list, maxUpdates);
	}

	@Override
	protected void work() {
		this.list.add(this.numberOfUpdates.get());
	}

}
