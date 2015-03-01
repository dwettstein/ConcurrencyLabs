package Lab01;

import java.lang.Thread;

/**
 * @author dwettstein
 *
 */
public class Ex1NoSync {
	
	private static long counter = 0;
	private static final long ITERATIONS = 10;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long nThreads = Integer.decode(args[0]);
		long mThreads = Integer.decode(args[1]);
		
		long startTime = System.nanoTime();
		
		// 100'000 iterations
		for (long i = 0; i < ITERATIONS; i++) {
			// Create the threads according to the program arguments 0 (n) and 1 (m).
			
		}
		
		long endTime = System.nanoTime();
		
		long programDuration = endTime - startTime;
		System.out.println("Program duration (nanotime): " + programDuration);
	}
	
	private void increment() {
		long tempValue = counter;
		tempValue = tempValue + 1;
		counter = tempValue;
	}
	
	private void decrement() {
		long tempValue = counter;
		tempValue = tempValue - 1;
		counter = tempValue;
	}
	
	private class IncrementThread extends Thread {
		private boolean isStopped = false;
		
		public void run() {
			while(!isStopped) {
				
			}
		}
		
		public void stopThread () {
			isStopped = true;
		}
	}
}