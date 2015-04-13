package assignment3.ex2;

import java.util.ArrayList;

public class Ex2 {
	// default program values:
	private static int numberOfThreads = 4; 
	private static int queueSize = 10;
	private static String queueType = "NoLockQueue";
	private static int numberOfRuns = 3;
	private static enum QueueTypes {
		NoLockQueue, SingleLockQueue, TwoLockQueue
	}
	
	private ArrayList<QueueWorker> allWorkers;
	private IQueue queue;
	
	
	public Ex2() {
		allWorkers = new ArrayList<QueueWorker>();
		
		QueueTypes currentQueueType = QueueTypes.valueOf(queueType);
		
		switch (currentQueueType) {
		case NoLockQueue: {
			queue = new NoLockQueue(queueSize);
			break;
		}
		case SingleLockQueue: {
			queue = new SingleLockQueue(queueSize);
			break;
		}
		case TwoLockQueue: {
			queue = new TwoLockQueue(queueSize);
			break;
		}
		default: {
			queue = new NoLockQueue(queueSize);
			break;
		}
		}
		
		// Create threads and add them to the list.
		for (int i = 1; i <= (numberOfThreads / 2); i++) {
			EnqueueWorker enqueueWorker = new EnqueueWorker(queue);
			this.allWorkers.add(enqueueWorker);
			
			DequeueWorker dequeueWorker = new DequeueWorker(queue);
			this.allWorkers.add(dequeueWorker);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 0) {
			numberOfThreads = Integer.decode(args[0]);
			queueSize = Integer.decode(args[1]);
			queueType = args[2].toString();
			if (args.length >= 4) {
				numberOfRuns = Integer.decode(args[3]);
			}
		}
		
		long totalTime = 0;
		for (int i = 0; i < numberOfRuns + 1; i++) {
			Ex2 ex2 = new Ex2();
			
			System.out.println("Starting program with '" + numberOfThreads + "' threads, queueSize '" + queueSize + "', and queueType '" + queueType + "'.");
			
			long startTime, endTime;
			startTime = System.nanoTime();
			
			for (QueueWorker worker : ex2.allWorkers) {
				worker.start();
			}
			
			for (QueueWorker worker : ex2.allWorkers) {
				worker.join();
			}
			
			endTime = System.nanoTime();
			
			long programDuration = endTime - startTime;
			System.out.println("Program duration (nanotime): '" + programDuration + "', in ms: '" + (programDuration / 1e6) + "'.");
			if (i > 0) {
				// Don't use the first run, since it takes a lot longer than the other runs.
				totalTime += programDuration;
			}
		}
		
		System.out.printf("Average program duration in ms: '%.3f'. Program options: threads '" + numberOfThreads + "', queueSize '" + queueSize + "' and queueType '" + queueType + "'.\n", (totalTime / numberOfRuns / 1e6));
	}

}
