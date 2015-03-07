package assignment1.ex1.archive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author dwettstein
 *
 */
public class IncrementThread extends Thread {
	private boolean isStopped = false;
	private Method methodToRun = null;
	
	public void run() {
		//Ex1NoSync.increment();
		
		// Run the method loaded dynamically by prepareRun method.
		try {
			methodToRun.invoke(null, null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		while(!isStopped) {
//			
//		}
	}
	
	/**
	 * Dynamically loads the increment method of given class.
	 * 
	 * @param className
	 */
	public void prepareRun(String className) {
		try {
			ClassLoader classLoader = this.getContextClassLoader();
			Class<?> loadedClass = classLoader.loadClass(className);
			methodToRun = loadedClass.getMethod("increment", null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopThread () {
		isStopped = true;
	}
}
