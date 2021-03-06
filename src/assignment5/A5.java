package assignment5;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class A5 {
	// default program values:
	protected static int numberOfThreads = 4;
	protected static AtomicInteger syncCounter = new AtomicInteger(0);	// A simple barrier to suspend all threads until the last thread has started.
	public static int listSize = 1000;
	private static boolean useLock = false;
	private static int numberOfRuns = 3;
	
	public ArrayList<AbstractWorker> allWorkers;
	public IQueue queue;
	
	
	public A5() {
		allWorkers = new ArrayList<AbstractWorker>();
		
		if (useLock) {
			queue = new LockBasedUnboundedQueue();
		}
		else {
			queue = new LockFreeUnboundedQueue();
		}
		
		int sizePerThread = listSize / numberOfThreads;
		int lowestNumber = 0;
		int highestNumber = 100;
		
		// Create threads and add them to the list.
		for (int i = 1; i <= (numberOfThreads / 2); i++) {
			EnqueueWorker insertWorker = new EnqueueWorker(queue, A5.generateIntegers(sizePerThread, lowestNumber, highestNumber));
			allWorkers.add(insertWorker);
			
			DequeueWorker removeWorker = new DequeueWorker(queue, A5.generateIntegers(sizePerThread, lowestNumber, highestNumber));
			allWorkers.add(removeWorker);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 0) {
			numberOfThreads = Integer.decode(args[0]);
			listSize = Integer.decode(args[1]);
			if (args[2].matches("useLock")) {
				useLock = true;
			}
			if (args.length >= 4) {
				numberOfRuns = Integer.decode(args[3]);
			}
		}
		
		long totalTime = 0;
		int totalItemsLeft = 0;
		int runsToSkip = 2;
		for (int i = 0; i < numberOfRuns + runsToSkip; i++) {
			A5 a5 = new A5();
			
			System.out.println("Starting program with '" + numberOfThreads + "' threads, listSize '" + listSize + "', updatesPerThread '" + listSize / numberOfThreads + "', useLock '" + useLock + "' and numberOfRuns '" + numberOfRuns + "'.");
			
			long startTime, endTime;
			startTime = System.nanoTime();
			
			for (AbstractWorker worker : a5.allWorkers) {
				worker.start();
			}
			
			for (AbstractWorker worker : a5.allWorkers) {
				worker.join();
			}
			
			endTime = System.nanoTime();
			
			long programDuration = endTime - startTime;
			System.out.println("Program duration (nanotime): '" + programDuration + "', in ms: '" + (programDuration / 1e6) + "'.");
			int itemsLeft = a5.queue.getSize();
			System.out.println("Final list (length: " + itemsLeft + ").");
			//System.out.println(a5.queue.toString());
			if (i > runsToSkip - 1) {
				// Don't use the runsToSkip runs, since they take a lot longer than the other runs.
				totalTime += programDuration;
				totalItemsLeft += itemsLeft;
			}
		}
		
		System.out.printf("Average program duration in ms: '%.3f'. Average items left in queue: '" + (totalItemsLeft / numberOfRuns) + "'. Program options: threads '" + numberOfThreads + "', listSize '" + listSize + "', useLock '" + useLock + "' and numberOfRuns '" + numberOfRuns + "'.\n", (totalTime / numberOfRuns / 1e6));
	}
	
	
	public static int[] generateIntegers(int size, int lowestInt, int highestInt) {
		int[] intArray = new int[size];
		Random randomGenerator = new Random();
		
		for (int i = 0; i < intArray.length; i++) {
			int randomInt = randomGenerator.nextInt(highestInt - lowestInt + 1) + lowestInt;
			intArray[i] = randomInt;
		}
		return intArray;
	}
}
