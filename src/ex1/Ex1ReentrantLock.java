package ex1;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dwettstein
 *
 */
public class Ex1ReentrantLock {
	
	private static long counter = 0;
	public static final long ITERATIONS = 10000;
	
	private static ReentrantLock lock;
	
	public Ex1ReentrantLock() {
		lock = new ReentrantLock();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		long nThreads = 4;
		long mThreads = 4;
		long iterations = ITERATIONS;
		if (args.length != 0) {
			nThreads = Integer.decode(args[0]);
			mThreads = Integer.decode(args[1]);
			iterations = Integer.decode(args[2]);
		}
		
		Ex1ReentrantLock ex1ReentrantLock = new Ex1ReentrantLock();
		
		System.out.println("Starting program with '" + nThreads + "' IncrementThreads, '" + mThreads + "' DecrementThreads and '" + iterations + "' iterations.");
		
		long startTime, endTime;
		startTime = System.nanoTime();
		
		for (long i = 0; i < iterations; i++) {
			// Create the threads according to the program arguments 0 (n) and 1 (m).
			ArrayList<Thread> allThreads = new ArrayList<Thread>();
			for (int n = 0; n < nThreads; n++) {
				IncrementThread localThread = ex1ReentrantLock.new IncrementThread();
				allThreads.add(localThread);
			}
			for (int m = 0; m < mThreads; m++) {
				DecrementThread localThread = ex1ReentrantLock.new DecrementThread();
				allThreads.add(localThread);
			}
			
			for (Thread thread : allThreads) {
				thread.start();
			}
			
			boolean isThreadRunning = true;
			while (isThreadRunning) {
				for (Thread thread : allThreads) {
					if (thread.isAlive()) {
						isThreadRunning = true;
					}
					else {
						isThreadRunning = false;
					}
				}
			}
			if (i % 1000 == 0) {
				System.out.println("Counter after '" + i + "' iteration(s): " + counter);
			}
		}
		
		endTime = System.nanoTime();
		
		long programDuration = endTime - startTime;
		System.out.println("Program duration (nanotime): " + programDuration);
		System.out.println("Counter has finally the value: " + counter);
	}
	
	public static void increment() {
		lock.lock();
		
		long tempValue = counter;
		tempValue = tempValue + 1;
		counter = tempValue;
		//System.out.println("Counter (incremented): " + counter);
		
		lock.unlock();
	}
	
	public static void decrement() {
		lock.lock();
		
		long tempValue = counter;
		tempValue = tempValue - 1;
		counter = tempValue;
		//System.out.println("Counter (decremented): " + counter);
		
		lock.unlock();
	}
	
	public class IncrementThread extends Thread {
		public void run() {
			Ex1ReentrantLock.increment();
		}
	}
	
	public class DecrementThread extends Thread {		
		public void run() {
			Ex1ReentrantLock.decrement();
		}
	}
}