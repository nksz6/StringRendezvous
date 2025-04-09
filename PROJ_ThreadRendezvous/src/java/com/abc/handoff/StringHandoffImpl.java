//ICS440
//Nick Kelley
//String Rendezvous Project

//Package
package com.abc.handoff;

//Imports
import com.abc.pp.stringhandoff.*;
import com.programix.thread.*;

//Provided class
public class StringHandoffImpl implements StringHandoff {
    //Additions:
    //State Variables:
    private String message = null;  //holds the message being passed from one thread to another.
                                    //when null, no message is currently available for hand-off.
                                    //when !null, contains the message a parser has left for the receiver.

    private boolean isPassing = false;  //flag to indicate whether a thread is in the process of passing a message.
                                        //will prevent multiple threads from trying to pass simultaneously.
                                        //(enforces one-passer-at-a-time requirement.)

    private boolean isReceiving = false;    //flag to track whether a thread is in the process of receiving a message.
                                            //(enforces one-receiver-at-a-time requirement.)

    private boolean isShutdown = false;     //indicates whether the StringHandoff has been shutdown.
                                            //when true, all pass and receive operations should immediately throw a ShutdownException.

    //Provided
    public StringHandoffImpl() {
    }

    //Pass Method
    @Override
    public synchronized void pass(String msg, long msTimeout) throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        //Additions:

        //Initial Checks:

        //Shutdown Check
        //If the StringHandoff has been shutdown, if so immediately throw a ShutdownException.
        //Check for shutdown - 2nd precedence but no wait() yet.
        if (isShutdown) {
            throw new ShutdownException();
        }

        //Single Passer Check
        //Check to make sure another thread isn't already in the process of passing
        if (isPassing) {
            throw new IllegalStateException("Another thread is already passing a message."); //If so, throw an Illegal State Exception
        }

        //If initial checks passed, we can start passing.

        //Set passing flag
        //Mark that this thread is now passing with a flag.
        isPassing = true;

        //If there's already a message in the slot (message != null) and there isn't a shutdown,
        //release the lock until another thread calls notify() or notifyAll() (receiver picks it up)
        try {

            //Timeout check!
            //Calculate end time for timeout
            long msEndTime;             //msEndTime stores the time-stamp of when the timeout will expire.
            if (msTimeout == 0L) {      //check if msTimeout is 0L (never timeout or wait 'infinitely')
                msEndTime = Long.MAX_VALUE; //in that case, set msEndTime to Long.MAX_VALUE
            } else {
                //if msTimeout is not 'infinity'
                msEndTime = System.currentTimeMillis() + msTimeout; //set the endtime to the current time, plus the time when the timeout should expire
            }
            //Above is equivalent to:
            //long msEndTime = msTimeout == 0L ? Long.MAX_VALUE : System.currentTimeMillis() + msTimeout;
            //msEndTime = msTimeout == 0L ? Long.MAX_VALUE : System.currentTimeMillis() + msTimeout;

            //wait until there is a receiver or timeout
            while (message != null && !isShutdown) {    //while message is not null and no shutdown
                if (msTimeout == 0L) { //if timeout is 'infinite'
                    wait();     //wait indefinitely
                } else {    //if not infinite, the time remaining is taking the time remaining calculation - the current time
                    long msRemaining = msEndTime - System.currentTimeMillis();

                    //if no time remains, throw a TimeoutException
                    if (msRemaining <= 0) {
                        throw new TimedOutException("timed out, no time remaining.");
                    }

                    //Note: because of the way I'm doing this,
                    //it shouldn't matter if the msTimeout was negative,
                    //because if the timeout was already supposed to happen,
                    //thats a timeout exception.
                    //otherwise, call wait for the remaining time.
                    wait(msRemaining);
                }

                //Post-'wait()' Checking conditions after waking up.

                //After waking up from the wait, first make sure there are no interuptions
                //1st Precedence
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                //then check for shutdown
                //2nd Precedence
                if (isShutdown) {
                    throw new ShutdownException();
                }
            }

            //Store Message and Notify receivers
            //store the message in the shared variable and notify all waiting threads
            //(which includes potential receivers), that there's a change in state.
            message = msg;
            notifyAll();
            //Wait until message is picked up
            while(message != null && !isShutdown) { //while message is not null and no shutdown
                if (msTimeout == 0L) { //if msTimeout is infinite
                    wait(); //wait indefinitely
                } else { //else
                    //redeclare msRemaining since this is another else block
                    long msRemaining = msEndTime - System.currentTimeMillis();
                    if (msRemaining <= 0) { //if time remaining is less than or equal to zero
                        //throw exception as before.
                        throw new TimedOutException("timed out, no time remaining.");
                    }
                    //otherwise wait out the remaining time
                    wait(msRemaining);
                }
                //Most post-'wait()' checks
                //After waking up from the wait, make sure there still aren't any interruptions or shutdowns as before.

                //InterruptedException - 1st precedence
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                //ShutdownException - 2nd precedence
                if (isShutdown) {
                    throw new ShutdownException();
                }
            }
            //Finally, make sure the isPassing flag gets reset.
            //Happens even if there was an exception so that the system doesn't get stuck.
        } finally {

            //reset the passing flag before exiting, even if an exception was thrown.
            isPassing = false;
        }
    }


    //Provided
    @Override
    public synchronized void pass(String msg) throws InterruptedException, ShutdownException, IllegalStateException {

        //Addition
        pass(msg, 0L); //timeout of (0L) = wait indefinitely
    }


    //Receive Method
    @Override
    public synchronized String receive(long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        //Additions

        //Shutdown Check
        //Make sure StringHandoff hasn't been shutdown, if so throw a ShutdownException.
        //Check for shutdown
        if (isShutdown) {
            throw new ShutdownException();
        }

        //Receiving Check
        //Check if another thread is already receiving, if so throw IllegalStateException.
        if (isReceiving) {
            throw new IllegalStateException("Another thread is already receiving a message");
        }

        //Set Receiving flag that we're ready to receive
        isReceiving = true;

        //start of try block
        try {

          //Timeout check!
          //see comments in pass function
            long msEndTime;
            if (msTimeout == 0L) {
                msEndTime = Long.MAX_VALUE;
            } else {
                msEndTime = System.currentTimeMillis() + msTimeout; //set the end-time to the current time, plus the time when the timeout should expire
            }
            //equivalent to:
            //long msEndTime = msTimeout == 0L ? Long.MAX_VALUE : System.currentTimeMillis() + msTimeout;


            //Wait until there's a message available ('message == null') and String Hand-off isn't shutdown.
            while (message == null && !isShutdown) {
                if (msTimeout == 0L) {
                    wait();
                } else {
                    //calling it 'msTimeRemaining' this time
                    long msTimeRemaining = (msEndTime - System.currentTimeMillis());
                    if (msTimeRemaining <= 0) {
                        throw new TimedOutException("timed out, no time remaining.");
                    }
                    wait(msTimeRemaining);
                } //end of else

                //Check for interrupt or shutdown after waking up (in order of precedence)
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (isShutdown) {
                    throw new ShutdownException();
                }
            } //end of while

            //check for shutdown again if in-case it ended early.
            if (isShutdown) {
                throw new ShutdownException();
            }

            //otherwise should have a message

            //Save the message as 'result
            String result = message;

            //Clear the message
            message = null;

            //Let passing threads know the 'message' slot is now empty.
            notifyAll();

            //Return the message passed to the Reciever as 'result'
            return result;

            //finally, reset the flag regardless if an exception was thrown.
        } finally {
            // Always reset the receiving flag before exiting
            isReceiving = false;
        }
    }


    //Provided
    @Override
    public synchronized String receive() throws InterruptedException, ShutdownException, IllegalStateException {

        //Addition
        return receive(0L); //timeout of (0L) = "wait indefinitely"
    }


    //Provided
    @Override
    public synchronized void shutdown() {

        //if shutdown
        isShutdown = true; //If shutdown() got called, set the flag to true.
        notifyAll(); //Wake up all the waiting threads.
    }


    //Provided
    @Override
    public Object getLockObject() {
        return this;
    }
}