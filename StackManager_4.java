package A2;

import java.util.Vector;

import CharStackExceptions.*;

public class StackManager_4 {
	// The Stack
	private static CharStack stack = new CharStack();
	private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
	private static final int NUM_PROBERS = 1; // Number of threads dumping stack
	private static int iThreadSteps = 3; // Number of steps they take
	private static int countPro=0; // count how many times producer is called
	// Semaphore declarations. Insert your code in the following:
	// ...
	private static Semaphore semaphorePro = new Semaphore(1);// I set it 1 so that one Producer Thread run at the same time
	private static Semaphore semaphoreCon = new Semaphore(); // I set it 0 so that Producer Thread run first	
	private static Semaphore semaphoreChar = new Semaphore(1); // I set it 1 so that it Prints after Producer and Consumer Thread finish
	// ...
	// The main()

	public static void main(String[] argv) {
		// Some initial stats...
		try {
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + stack.getTop() + ".");
			System.out.println("Initial value of stack top = " + stack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");
		} catch (CharStackEmptyException e) {
			System.out.println("Caught exception: StackCharEmptyException");
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
		/*
		 * The birth of threads
		 */
		Consumer ab1 = new Consumer();
		Consumer ab2 = new Consumer();
		System.out.println("Two Consumer threads have been created.");
		Producer rb1 = new Producer();
		Producer rb2 = new Producer();
		System.out.println("Two Producer threads have been created.");
		CharStackProber csp = new CharStackProber();
		System.out.println("One CharStackProber thread has been created.");
		/*
		 * start executing
		 */
		ab1.start();
		rb1.start();
		ab2.start();
		rb2.start();
		csp.start();
		/*
		 * Wait by here for all forked threads to die
		 */
		try {
			ab1.join();
			ab2.join();
			rb1.join();
			rb2.join();
			csp.join();
			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + stack.getTop() + ".");
			System.out.println("Final value of stack top = " + stack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
			System.out.println("Stack access count = " + stack.getAccessCounter()); // add code for this
		} catch (InterruptedException e) {
			System.out.println("Caught InterruptedException: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Caught exception: " + e.getClass().getName());
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
	} // main()
	/*
	 * Inner Consumer thread class
	 */

	static class Consumer extends BaseThread {
		private char copy; // A copy of a block returned by pop()

		public void run() {
			System.out.println("Consumer thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < StackManager_4.iThreadSteps; i++) {
				// Insert your code in the following:
				// ...				
				try {
					semaphoreCon.Wait(); // enter critical section:
					semaphoreChar.Wait();
					this.copy = stack.pop(); // pop top of stack
				} catch (CharStackEmptyException fe) {
					// fe.printStackTrace();
					i--; // redo this iteration next time since it was "missed"
				} catch (Exception e) {
					e.printStackTrace();
					i--; // redo this iteration next time since it was "missed"
				} finally {						
					System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);
					semaphoreChar.Signal();
					semaphoreCon.Signal(); // end of critical section
				}
				// ...
            }			
			System.out.println("Consumer thread [TID=" + this.iTID + "] terminates.");
		}
	} // class Consumer
	/*
	 * Inner class Producer
	 */

	static class Producer extends BaseThread {
		private char block; // block to be returned

		public void run() {
			System.out.println("Producer thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < StackManager_4.iThreadSteps; i++) {
				// Insert your code in the following:
				// ...				
				try {
					semaphorePro.Wait(); // enter critical section:
					char top = stack.pick(); // read the top char in stack:
					this.block = (char) (top + 1); // get the next character in the (ascii) alphabet.
					stack.push(this.block); // push it onto the stack
				} catch (CharStackEmptyException ee) {
					this.block = 'a'; // if the stack is empty at pick() then the char 'a' must be written
					try {
						stack.push(this.block); // push it onto the stack
					} catch (CharStackFullException e) {
						i--; // redo this iteration next time since it was "missed"
					}
				} catch (CharStackFullException e) {
					i--; // redo this iteration next time since it was "missed"
				} catch (Exception e) {
					e.printStackTrace();
				} finally {						
					System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);
					semaphorePro.Signal(); // end of critical section
				}
			}
				// ...
			System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");		
			if(countPro == 1) {
				semaphoreCon.Signal(); // end of critical section		
			}else {
				countPro++;
			}
		}
	} // class Producer
	/*
	 * Inner class CharStackProber to dump stack contents
	 */

	static class CharStackProber extends BaseThread {
		public void run() {
			System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < 2 * StackManager_4.iThreadSteps; i++) {
				// Insert your code in the following. Note that the stack state must be
				// printed in the required format.
				// ...	
				try {
					semaphorePro.Wait(); // enter the critical section:
					semaphoreChar.Wait(); 					
					System.out.println("Stack S = ([" + stack.getAt(0) + "],["
							+ stack.getAt(1) + "],[" + stack.getAt(2) + "],["
							+ stack.getAt(3) + "],[" + stack.getAt(4) + "],["
							+ stack.getAt(5) + "],[" + stack.getAt(6) + "],["
							+ stack.getAt(7) + "],[" + stack.getAt(8) + "],[" 
							+ stack.getAt(9) + "])");
				} catch (CharStackInvalidAceessException e) {
					e.printStackTrace();
				} finally {
					semaphoreChar.Signal(); 
					semaphorePro.Signal(); // end of critical section
				}
				// ...
			}
			System.out.println("");
		}
	} // class CharStackProber
}
