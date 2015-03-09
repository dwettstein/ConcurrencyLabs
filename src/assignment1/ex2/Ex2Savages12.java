package assignment1.ex2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;


/**
 * @author David
 *
 */
public class Ex2Savages12 {
	private static int maxNumberOfRefills = 10;
	
	private static int potCapacityInput = 10;
	
	private static int maxNumberOfSavages = 3;
	private static int maxNumberPortionsToEat = -1; // Set to -1 for infinite.
	
	Pot pot;
	Cook cook;
	ArrayList<Savage> savages;
	
	public Ex2Savages12() {
		pot = new Pot();
		cook = new Cook();
		savages = new ArrayList<Savage>();
		
		for (int i = 0; i < maxNumberOfSavages; i++) {
			Savage savage = new Savage();
			savages.add(savage);
		}
		
		cook.start();
		
		for (Savage savage : savages) {
			savage.start();
		}
	}
	
	/**
	 * The constants of this program can be set with the program parameters.
	 * savages portionsToEat potCapacity refills
	 * e.g. 10 -1 5 10
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 0) {
			maxNumberOfSavages = Integer.decode(args[0]);
			maxNumberPortionsToEat = Integer.decode(args[1]);
			potCapacityInput = Integer.decode(args[2]);
			maxNumberOfRefills = Integer.decode(args[3]);
		}
		
		new Ex2Savages12();
	}
	
	/**
	 * The pot is the shared resource.
	 * 
	 * All the methods of the pot have to be called within the mutexLock.
	 * 
	 * @author David
	 *
	 */
	public class Pot {
		public Semaphore mutexLock;
		
		public Semaphore portions;
		public Semaphore refillNeeded;
		
		public int potCapacity = potCapacityInput;
		private int portionsInPot;

		public Pot() {
			this.mutexLock = new Semaphore(1);
			this.portions = new Semaphore(this.potCapacity);
			this.refillNeeded = new Semaphore(0);
			this.portionsInPot = this.potCapacity;
		}
		
		public void decreasePortionsInPot() {
			this.portionsInPot--;
		}
		
		public int getPortionsInPot() {
			return this.portionsInPot;
		}
		
		public void setPortionsInPot(int newPortions) {
			this.portionsInPot = newPortions;
		}
		
		public boolean isEmpty() {
			return (this.portionsInPot <= 0);
		}
	}
	
	public class Cook extends Thread {
		private boolean isStopped = false;
		private int refillCounter = 0;
		
		public void run() {
			while(!isStopped) {
				try {
					pot.refillNeeded.acquire();
					
					// Refill the pot with mutex if it is empty.
					pot.mutexLock.acquire();
					
					if (!pot.isEmpty()) {
						System.out.println("There are still some portions in the pot: '" + pot.getPortionsInPot() + "'.");
					}
					else {
						pot.setPortionsInPot(pot.potCapacity);
						refillCounter++;
						System.out.println("Filled the pot with '" + pot.potCapacity + "' portions already '" + refillCounter + "' times. New number of portions in pot: '" + pot.getPortionsInPot() + "'.");
						
						if (refillCounter >= maxNumberOfRefills) {
							System.out.println("The cook refilled the pot now '" + refillCounter + "' times. Now stopping to cook...");
							this.stopThread();
						}
						// The cook has just refilled the pot. Remove other permits.
						pot.refillNeeded.drainPermits();
						
						// Inform the waiting savages.
						pot.portions.release(pot.potCapacity);
					}
					
					pot.mutexLock.release();
					
					
				} catch (InterruptedException e) {
					System.out.println("The cook was interrupted. He stops working now.");
					this.stopThread();
				}
			}
		}
		
		public void stopThread () {
			isStopped = true;
		}
		
		public boolean isStopped() {
			return this.isStopped;
		}
	}
	
	public class Savage extends Thread {
		private boolean isStopped = false;
		private int numberPortionsEat = 0;
		
		public void run() {
			while(!isStopped) {
				if(pot.portions.tryAcquire()) {
					eat();
				}
				else {
					if (cook.isStopped()) {
						System.out.println("The cook stopped working. Now stopping to eat...");
						this.stopThread();
					}
					else {
						pot.refillNeeded.release();
						try {
							pot.portions.acquire();
							eat();
						} catch (InterruptedException e) {
							System.out.println("This savage was interrupted. He stops eating now.");
							this.stopThread();
						}
					}
				}
			}
		}
		
		private void eat() {
			try {
				// Eat one from the pot with mutex.
				pot.mutexLock.acquire();
				pot.decreasePortionsInPot();
				this.numberPortionsEat++;
				System.out.println("Decreased portions in pot. New number of portions in pot: '" + pot.getPortionsInPot() + "'.");
				
				if (pot.isEmpty() && cook.isStopped()) {
					// Inform the waiting threads that the last portion was eaten.
					for (Savage savage : savages) {
						if (savage != this) {
							savage.interrupt();
						}
					}
				}
				
				pot.mutexLock.release();
				
				if (maxNumberPortionsToEat != -1 && this.numberPortionsEat >= maxNumberPortionsToEat) {
					this.stopThread();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void stopThread () {
			isStopped = true;

			System.out.println("This savage has eaten '" + this.numberPortionsEat + "' portions from the pot.");
			
			// Have all savages stopped eating?
			boolean allStopped = true;
			for (Savage savage : savages) {
				// No changes to the list savages are allowed due to concurrent execution. Only read.
				if (!savage.isStopped) {
					allStopped = false;
				}
			}
			
			if(allStopped && !cook.isStopped()) {
				System.out.println("All savages have eaten enough. Tell the cook that he can stop working.");
				// Inform the cook, that he doesn't have to work anymore.
				cook.interrupt();
			}
		}
		
		public void notifyMyself() {
			this.notify();
		}
	}
	
}