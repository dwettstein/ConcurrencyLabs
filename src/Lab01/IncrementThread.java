package Lab01;

/**
 * @author dwettstein
 *
 */
public class IncrementThread extends Thread {
	private boolean isStopped = false;
	
	public void run() {
		Ex1NoSync.increment();
		
//		while(!isStopped) {
//			
//		}
	}
	
	public void stopThread () {
		isStopped = true;
	}
}
