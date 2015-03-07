package assignment1.ex2;

import java.util.concurrent.Semaphore;


/**
 * @author David
 *
 */
public class Ex2Savages1 {
	Pot pot;
	Cook cook;
	Savage savage;
	
	public Ex2Savages1() {
		pot = new Pot();
		cook = new Cook();
		savage = new Savage();
		
		cook.start();
		savage.start();
	}
	
	public static void main(String[] args) {
		new Ex2Savages1();
	}

	
	/**
	 * The pot is the shared resource.
	 * 
	 * @author David
	 *
	 */
	public class Pot {
		private Semaphore mutexLock;
		
		public Semaphore portions;
		public Semaphore refillNeeded;
		
		public final int MAX_PORTIONS = 10;
		private int portionsInPot;

		public Pot() {
			this.mutexLock = new Semaphore(1);
			this.portions = new Semaphore(MAX_PORTIONS);
			this.refillNeeded = new Semaphore(0);
			this.portionsInPot = MAX_PORTIONS;
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
		
		
	}
	
	public class Cook extends Thread {
		private boolean isStopped = false;
		
		public void run() {
			while(!isStopped) {
				try {
					pot.refillNeeded.acquire();
					
					// Refill the pot with mutex.
					pot.mutexLock.acquire();
					pot.setPortionsInPot(pot.MAX_PORTIONS);
					System.out.println("Filled pot with '" + pot.MAX_PORTIONS + "' portions. New number of portions in pot: '" + pot.getPortionsInPot() + "'.");
					pot.mutexLock.release();
					
					// Inform the waiting savages.
					pot.portions.release(pot.MAX_PORTIONS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopThread () {
			isStopped = true;
		}
	}
	
	public class Savage extends Thread {
		private boolean isStopped = false;
		
		public void run() {
			while(!isStopped) {
				if(pot.portions.tryAcquire()) {
					try {
						// Eat one from the pot with mutex.
						pot.mutexLock.acquire();
						pot.decreasePortionsInPot();
						System.out.println("Decreased portions in pot. New number of portions in pot: '" + pot.getPortionsInPot() + "'.");
						pot.mutexLock.release();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					pot.refillNeeded.release();
				}
			}
		}
		
		public void stopThread () {
			isStopped = true;
		}
	}
	
}