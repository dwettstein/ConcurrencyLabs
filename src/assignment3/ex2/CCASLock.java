package assignment3.ex2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * Implements a compare-and-compare-and-set lock similar to TTASLock
 * (test-and-test-and-set).
 * 
 * @author dwettstein
 *
 */
public class CCASLock implements java.util.concurrent.locks.Lock {
	private AtomicInteger lockState; // 0: unlocked, 1: locked

	public CCASLock() {
		this.lockState = new AtomicInteger(0);
	}

	@Override
	public void lock() {
		while (true) {
			while (this.lockState.get() == 1) {
				// Wait while lockState is locked (1).
			}
			if (this.lockState.compareAndSet(0, 1)) {
				// Lock acquired successfully.
				return;
			}
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		return this.lockState.compareAndSet(0, 1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit timeUnit)
			throws InterruptedException {
		if (timeUnit == null) {
			timeUnit = TimeUnit.MILLISECONDS;
		}
		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) <= time
				&& this.lockState.compareAndSet(0, 1)) {
			return true;
		}
		return false;
	}

	@Override
	public void unlock() {
		this.lockState.set(0);
	}
}