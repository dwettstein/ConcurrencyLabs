package assignment5;

import java.util.concurrent.locks.ReentrantLock;

public class Node {
	protected Object object;
	protected int key;
	protected Node next;
	
	protected ReentrantLock lock;
	
	public Node(int key) {
		this.object = new Object();
		this.key = key;
		this.lock = new ReentrantLock();
	}
	
	public Node(Object object) {
		this.object = object;
		this.key = object.hashCode();
		this.lock = new ReentrantLock();
	}
	
	public void setNextNode(Node next) {
		this.next = next;
	}
	
	public void lock() {
		this.lock.lock();
	}
	
	public void unlock() {
		this.lock.unlock();
	}

	public boolean hasNext() {
		if (this.next != null) {
			return true;
		}
		return false;
	}
	
	public int getKey() {
		return this.key;
	}
	
	public String toString() {
		return String.valueOf(this.key);
	}

	public boolean compareAndSet(Node expected, Node newNode) {
		// TODO Auto-generated method stub
		return false;
	}
}