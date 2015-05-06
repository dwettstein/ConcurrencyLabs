package assignment5;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
	protected Object object;
	protected int key;
	protected AtomicReference<Node> next;
	
	protected ReentrantLock lock;
	
	public Node(int key) {
		this.object = new Object();
		this.key = key;
		this.lock = new ReentrantLock();
		this.next = new AtomicReference<Node>(null);
	}
	
	public Node(Object object) {
		this.object = object;
		if (object == null) {
			this.key = Integer.MIN_VALUE;
		}
		else {
			this.key = object.hashCode();
		}
		this.lock = new ReentrantLock();
		this.next = new AtomicReference<Node>(null);
	}
	
	public void setNextNode(AtomicReference<Node> next) {
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
	
}