package assignment3.ex1;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * @author dwettstein
 *
 */
public class Ex1CASLock {
	// default program values:
	private static int numberOfThreads = 4; 
	private static int maxValueOfCounter = 1000;
	private static boolean useCCAS = false;
	private static boolean useVolatile = false;

	private ArrayList<IncrementThread> allIncThreads;
	private int[] threadAccesses;
	private Counter counter;
	
	public Ex1CASLock(int numberOfThreads) {
		allIncThreads = new ArrayList<IncrementThread>();
		threadAccesses = new int[numberOfThreads];
		
		if (useVolatile) {
			counter = new VolatileCounter(numberOfThreads);
		}
		else {
			counter = new NonVolatileCounter(numberOfThreads);
		}
		
		// Create threads and add them to the list.
		for (int i = 1; i <= numberOfThreads; i++) {
			IncrementThread thread = new IncrementThread();
			this.allIncThreads.add(thread);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 0) {
			numberOfThreads = Integer.decode(args[0]);
			maxValueOfCounter = Integer.decode(args[1]);
			if (args.length >= 3 && args[2].matches("useCCAS")) {
				useCCAS = true;
			}
			if (args.length >= 4 && args[3].matches("useVolatile")) {
				useVolatile = true;
			}
		}
		
		Ex1CASLock lock = new Ex1CASLock(numberOfThreads);
		
		System.out.println("Starting program with '" + numberOfThreads + "' threads, counter max value '" + maxValueOfCounter + "', useCCAS '" + useCCAS + "' and useVolatile '" + useVolatile + "'.");
		
		long startTime, endTime;
		startTime = System.nanoTime();
		
		for (IncrementThread thread : lock.allIncThreads) {
			thread.start();
		}
		
		for (IncrementThread thread : lock.allIncThreads) {
			thread.join();
		}
		
		endTime = System.nanoTime();
		
		long programDuration = endTime - startTime;
		System.out.println("Program duration (nanotime): '" + programDuration + "', in ms: '" + (programDuration / 1e6) + "'.");
		System.out.println("Counter has finally the value: " + lock.counter.getValue());
		for (IncrementThread thread : lock.allIncThreads) {
			int threadIntId = thread.getIntId();
			System.out.println("Thread '" + threadIntId + "' has modified the counter '" + lock.threadAccesses[threadIntId] + "' times.");
		}
	}
	
	/**
	 * Implements a compare-and-set lock similar to TASLock (test-and-set).
	 * 
	 * @author dwettstein
	 *
	 */
	public abstract class AbstractCASLock implements java.util.concurrent.locks.Lock {		
		AtomicInteger lockState; // 0: unlocked, 1: locked
		
		public AbstractCASLock() {
			this.lockState = new AtomicInteger(0);
		}

		@Override
		public abstract void lock();

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
			
			while ((System.currentTimeMillis() - startTime) <= time && this.lockState.compareAndSet(0, 1)) {
				return true;
			}
			return false;
		}

		@Override
		public void unlock() {
			this.lockState.set(0);
		}
	}


		public class CASLock extends AbstractCASLock {
		
		public CASLock() {
			super();
		}
		
		@Override
		public void lock() {
			while (this.lockState.compareAndSet(0, 1)) {
				// Lock acquired successfully.
			}
		}
	}
	
	/**
	 * Extends the compare-and-set lock with a additional compare.
	 * 
	 * @author dwettstein
	 *
	 */
	public class CCASLock extends AbstractCASLock {
		
		public CCASLock() {
			super();
		}
		
		@Override
		public void lock() {
			while(true) {
				while (this.lockState.get() == 1) {
					// Wait while lockState is locked (1).
				}
				if (this.lockState.compareAndSet(0, 1)) {
					// Lock acquired successfully.
					return;
				}
			}
		}
	}
	
	/**
	 * This counter represents the shared resource.
	 * 
	 * @author dwettstein
	 *
	 */
	public abstract class Counter {
		protected AbstractCASLock casLock;
		
		public Counter(int numberOfThreads) {
			if (useCCAS) {
				this.casLock = new CCASLock();
			}
			else {
				this.casLock = new CASLock();
			}
		}
		
		public abstract void increment(int threadIntId);
		
		public abstract long getValue();
	}
	
	public class NonVolatileCounter extends Counter {
		private long counterValue;
		
		public NonVolatileCounter(int numberOfThreads) {
			super(numberOfThreads);
			this.counterValue = 0;
		}
		
		public void increment(int threadIntId) {
			this.casLock.lock();
			if (this.counterValue < maxValueOfCounter) {
				this.counterValue++;
				threadAccesses[threadIntId]++;
			}
			else {
				// Stop all threads.
				for (IncrementThread thread : allIncThreads) {
					if (thread == Thread.currentThread()) {
						thread.stopThread();
					}
					else {
						thread.interrupt();
					}
				}
			}
			this.casLock.unlock();
		}
		
		public long getValue() {
			return this.counterValue;
		}
		
	}
	
	public class VolatileCounter extends Counter {
		private volatile long counterValue;
		
		public VolatileCounter(int numberOfThreads) {
			super(numberOfThreads);
			this.counterValue = 0;
		}
		
		public void increment(int threadIntId) {
			this.casLock.lock();
			if (this.counterValue < maxValueOfCounter) {
				this.counterValue++;
				threadAccesses[threadIntId]++;
			}
			else {
				// Stop all threads.
				for (IncrementThread thread : allIncThreads) {
					if (thread == Thread.currentThread()) {
						thread.stopThread();
					}
					else {
						thread.interrupt();
					}
				}
			}
			this.casLock.unlock();
		}
		
		public long getValue() {
			return this.counterValue;
		}
		
	}
	
	public class IncrementThread extends Thread {	
		private boolean isStopped = false;
		public int threadIntId = this.getIntId();
		
		public void run() {
			while(!isStopped) {
				counter.increment(this.threadIntId);
			}
		}
		
		public int getIntId() {
		    if (this.getId() < Integer.MIN_VALUE || this.getId() > Integer.MAX_VALUE) {
		        throw new IllegalArgumentException
		            (this.getId() + " cannot be cast to int without changing its value.");
		    }
		    return ((int) this.getId() % numberOfThreads);
		}
		
		public void stopThread () {
			isStopped = true;
		}
	}
	
}
