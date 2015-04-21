package assignment4;

import java.util.concurrent.locks.ReentrantLock;

public class Node {
	Object object;
	int key;
	Node next;
	
	ReentrantLock lock;
	
	public Node(int key) {
		// Sentinel node.
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
}