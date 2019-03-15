# Stack access using semaphore

### StackManager.java

In this class the producers and consumers are accessing the stack one at a time by locking stack

### StackManager_4.java

In this class the producers are ran first then the consumers start, two semaphores are using to control this, 
the producer semaphore is allowed to run only one thread initially and consumer semaphore is not alowed to run 
any thread at the begining. This allows the producer to run first and push all the characters in the stack.
