package A2;

public class Semaphore {
	private int value;

	public Semaphore(int value) {
		this.value = value;
	}

	public Semaphore() {
		this(0);
	}

	public synchronized void Wait() {	
		this.value--;
		// 1.	The decremental value need to be at the top instead of at the bottom, 
		//because if placed at the bottom then many threads might be waiting and the value will 
		//not update the number of waiting threads, it will only decrement the value of threads 
		//that acquired authorization and might never go below zero.
		
		if (this.value < 0) { 
			//2.	The while statement is changed to if condition so that a process to do not 
			//do repeated wait. With the if condition all the threads enter into waiting list 
			//without repetition and can be called to run again using the notify().  
			
			//3.	The "<= 0" was changed to "< 0", because 0 means that it was 1 (i.e. semaphore 
			//is free) at the beginning of the function--the current process just made it 0 with the 
			//first decrement statement.

			try {
				wait(); 
				//4.	Any thread that reached this point is on the waiting list. If the while 
				//statement was kept it would have resulted in a deadlock—because any thread, 
				//upon notification, would run back into the while loop and wait() again since 
				//the semaphore's value is still negative even if it was just incremented by the Signal() function.
			} catch (InterruptedException e) {
				System.out.println("Semaphore::Wait() - caught InterruptedException: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public synchronized void Signal() {
		++this.value;
		notify();	
	}

	public synchronized void P() {
		this.Wait();
	}

	public synchronized void V() {
		this.Signal();
	}
}
