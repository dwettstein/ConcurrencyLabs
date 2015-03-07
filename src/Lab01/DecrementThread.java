/**
 * 
 */
package Lab01;

/**
 * @author dwettstein
 *
 */
public class DecrementThread extends Thread {
	private boolean isStopped = false;
	
	public void run() {
		Ex1NoSync.decrement();
		
//		while(!isStopped) {
//			
//		}
	}
	
	public void stopThread () {
		isStopped = true;
	}
}
