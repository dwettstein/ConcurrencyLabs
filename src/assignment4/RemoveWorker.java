package assignment4;

public class RemoveWorker extends ListWorker {

	public RemoveWorker(ISet list, int maxUpdates) {
		super(list, maxUpdates);
	}

	@Override
	protected void work() {
		this.list.remove(this.numberOfUpdates.get());
	}

}
