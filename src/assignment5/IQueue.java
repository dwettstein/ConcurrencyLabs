package assignment5;

public interface IQueue {
	public void enq(Object item);

	public Object deq();
	
	public int getSize();
}