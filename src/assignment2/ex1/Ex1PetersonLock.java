package assignment2.ex1;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Ex1PetersonLock {
	// default program values:
	private static int numberOfThreads = 4; 
	private static int maxValueOfCounter = 1000;
	private static boolean useVolatile = false;
	private static boolean useSolarisAffinity = false;
	
	private ArrayList<IncrementThread> allIncThreads;
	private int[] threadAccesses;
	private Counter counter;
	
	public Ex1PetersonLock(int numberOfThreads) {
		allIncThreads = new ArrayList<IncrementThread>();
		threadAccesses = new int[numberOfThreads];
		
		if (useVolatile) {
			counter = new VolatileCounter(numberOfThreads);
		}
		else {
			counter = new NonVolatileCounter(numberOfThreads);
		}
		
		if (useSolarisAffinity) {
			setSolarisAffinity();
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
			if (args.length >= 3 && args[2].matches("useVolatile")) {
				useVolatile = true;
			}
			if (args.length >= 4 && args[3].matches("useSolarisAffinity")) {
				useSolarisAffinity = true;
			}
		}
		
		Ex1PetersonLock ex1 = new Ex1PetersonLock(numberOfThreads);
		
		System.out.println("Starting program with '" + numberOfThreads + "' threads, counter max value '" + maxValueOfCounter + "', useVolatile '" + useVolatile + "' and useSolarisAffinity '" + useSolarisAffinity + "'.");
		
		long startTime, endTime;
		startTime = System.nanoTime();
		
		for (IncrementThread thread : ex1.allIncThreads) {
			thread.start();
		}
		
		for (IncrementThread thread : ex1.allIncThreads) {
			thread.join();
		}
		
		endTime = System.nanoTime();
		
		long programDuration = endTime - startTime;
		System.out.println("Program duration (nanotime): '" + programDuration + "', in ms: '" + (programDuration / 1e6) + "'.");
		System.out.println("Counter has finally the value: " + ex1.counter.getValue());
		for (IncrementThread thread : ex1.allIncThreads) {
			int threadIntId = thread.getIntId();
			System.out.println("Thread '" + threadIntId + "' has modified the counter '" + ex1.threadAccesses[threadIntId] + "' times.");
		}
	}
	
	/**
	 * Implements the Peterson's generalized algorithm for n processes.
	 *
		 lock() {
			for (int L = 1; L < n; L++) {
				level[i] = L;
				victim[L] = i;
				while (victim [L] == i && exists k != i with level[k] >= L) {
					// wait
				};
			}
		}
		
		unlock() {
			level[i] = 0;
		}
	 * 
	 * @author David
	 *
	 */
	public class PetersonLock {		
		AtomicIntegerArray levelArray;
		AtomicIntegerArray victimArray;
		int numberOfThreads;
		
		public PetersonLock(int numberOfThreads) {
			this.levelArray = new AtomicIntegerArray(numberOfThreads);
			this.victimArray = new AtomicIntegerArray(numberOfThreads);
			this.numberOfThreads = numberOfThreads;
		}
		
		private boolean threadWithHigherLevelExists(int threadIntId, int level) {
			for (int k = 0; k < this.numberOfThreads; k++) {
				if (k == threadIntId) {
					continue;
				}
				if (this.levelArray.get(k) >= level) {
					// If a thread with higher level than the current exists, return true.
					return true;
				}
			}
			return false;
		}
		
		public void lock(int threadIntId) {
			for (int level = 1; level < this.numberOfThreads; level++) {
				this.levelArray.set(threadIntId, level);
				this.victimArray.set(level, threadIntId);
				while (this.victimArray.get(level) == threadIntId && threadWithHigherLevelExists(threadIntId, level)) {
					// wait
				}
			}
		}
		
		public void unlock(int threadIntId) {
			levelArray.set(threadIntId, 0);
		}
	}

	/**
	 * This counter represents the shared resource.
	 * 
	 * @author David
	 *
	 */
	public abstract class Counter {
		protected PetersonLock petersonLock;
		
		public Counter(int numberOfThreads) {
			this.petersonLock = new PetersonLock(numberOfThreads);
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
			this.petersonLock.lock(threadIntId);
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
			this.petersonLock.unlock(threadIntId);
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
			this.petersonLock.lock(threadIntId);
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
			this.petersonLock.unlock(threadIntId);
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
	
	/**
	 * Forces to use only one processor on Solaris OS (Sun Fire T2000).
	 * 
	 * Method was taken from assignment 2 sheet.
	 */
	public static void setSolarisAffinity() {
		try {
			// retrieve process id
			String pid_name = java.lang.management.ManagementFactory
					.getRuntimeMXBean().getName();
			String[] pid_array = pid_name.split("@");
			int pid = Integer.parseInt(pid_array[0]);
			// random processor
			int processor = new java.util.Random().nextInt(32);
			// Set process affinity to one processor (on Solaris)
			Process p = Runtime.getRuntime().exec(
					"/usr/sbin/pbind -b " + processor + " " + pid);
			p.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
}
