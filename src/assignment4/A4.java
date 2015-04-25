package assignment4;

import java.util.ArrayList;
import java.util.Random;

public class A4 {
	// default program values:
	private static int numberOfThreads = 4; 
	public static int listSize = 1000;
	private static boolean useOptimistic = false;
	private static int numberOfRuns = 3;
	
	public ArrayList<ListWorker> allWorkers;
	public ISet list;
	
	
	public A4() {
		allWorkers = new ArrayList<ListWorker>();
		
		if (useOptimistic) {
			list = new OptimisticFineGrainedLockList();
		}
		else {
			list = new FineGrainedLockList();
		}
		
		int sizePerThread = listSize / numberOfThreads;
		int lowestNumber = 0;
		int highestNumber = 100;
		
		// Create threads and add them to the list.
		for (int i = 1; i <= (numberOfThreads / 2); i++) {
			InsertWorker insertWorker = new InsertWorker(list, A4.generateIntegers(sizePerThread, lowestNumber, highestNumber));
			allWorkers.add(insertWorker);
			
			RemoveWorker removeWorker = new RemoveWorker(list, A4.generateIntegers(sizePerThread, lowestNumber, highestNumber));
			allWorkers.add(removeWorker);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 0) {
			numberOfThreads = Integer.decode(args[0]);
			listSize = Integer.decode(args[1]);
			if (args[2].matches("useOptimistic")) {
				useOptimistic = true;
			}
			if (args.length >= 4) {
				numberOfRuns = Integer.decode(args[3]);
			}
		}
		
		long totalTime = 0;
		for (int i = 0; i < numberOfRuns + 1; i++) {
			A4 a4 = new A4();
			
			System.out.println("Starting program with '" + numberOfThreads + "' threads, listSize '" + listSize + "', updatesPerThread '" + listSize / numberOfThreads + "', useOptimistic '" + useOptimistic + "' and numberOfRuns '" + numberOfRuns + "'.");
			
			long startTime, endTime;
			startTime = System.nanoTime();
			
			for (ListWorker worker : a4.allWorkers) {
				worker.start();
			}
			
			for (ListWorker worker : a4.allWorkers) {
				worker.join();
			}
			
			endTime = System.nanoTime();
			
			long programDuration = endTime - startTime;
			System.out.println("Program duration (nanotime): '" + programDuration + "', in ms: '" + (programDuration / 1e6) + "'.");
			System.out.println("Final list (length: " + a4.list.getSize() + "): \n" + a4.list.toString());
			if (i > 0) {
				// Don't use the first run, since it takes a lot longer than the other runs.
				totalTime += programDuration;
			}
		}
		
		System.out.printf("Average program duration in ms: '%.3f'. Program options: threads '" + numberOfThreads + "', listSize '" + listSize + "', useOptimistic '" + useOptimistic + "' and numberOfRuns '" + numberOfRuns + "'.\n", (totalTime / numberOfRuns / 1e6));
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
