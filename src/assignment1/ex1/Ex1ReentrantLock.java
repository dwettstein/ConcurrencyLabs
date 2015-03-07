package assignment1.ex1;

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
	
	private static long nThreads = 4;
	private static long mThreads = 4;
	private static long iterations = ITERATIONS;
	
	private static ArrayList<Thread> allThreads;
	
	private static ReentrantLock lock;
	
	public Ex1ReentrantLock() {
		lock = new ReentrantLock();
		allThreads = new ArrayList<Thread>();
		
		// Create the threads according to the program arguments 0 (n) and 1 (m).
		for (int n = 0; n < nThreads; n++) {
			IncrementThread localThread = this.new IncrementThread();
			allThreads.add(localThread);
		}
		for (int m = 0; m < mThreads; m++) {
			DecrementThread localThread = this.new DecrementThread();
			allThreads.add(localThread);
		}
		
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 0) {
			nThreads = Integer.decode(args[0]);
			mThreads = Integer.decode(args[1]);
			iterations = Integer.decode(args[2]);
		}
		
		new Ex1ReentrantLock();
		
		System.out.println("Starting program with '" + nThreads + "' IncrementThreads, '" + mThreads + "' DecrementThreads and '" + iterations + "' iterations.");
		
		long startTime, endTime;
		startTime = System.nanoTime();
		
		for (Thread thread : allThreads) {
			thread.start();
		}
		
		for (Thread thread : allThreads) {
			thread.join();
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
		lock.unlock();
	}
	
	public static void decrement() {
		lock.lock();
		long tempValue = counter;
		tempValue = tempValue - 1;
		counter = tempValue;
		lock.unlock();
	}
	
	public class IncrementThread extends Thread {
		public void run() {
			for (long i = 0; i < iterations; i++) {
				Ex1ReentrantLock.increment();
			}
		}
	}
	
	public class DecrementThread extends Thread {		
		public void run() {
			for (long i = 0; i < iterations; i++) {
				Ex1ReentrantLock.decrement();
			}
		}
	}
}