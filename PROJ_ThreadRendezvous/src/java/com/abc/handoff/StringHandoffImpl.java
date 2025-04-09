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

    //Provided
    @Override
    public synchronized void pass(String msg, long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        //Additions:
        //Initial Checks:

        //Timeout Check
        //initially implement this method to throw a RuntimeException if a non-zero timeout is provided.
        //(Replace later)
        if(msTimeout != 0L) {
            throw new RuntimeException("Non-zero timeout is not YET supported."); //For now, throw RunTime exception for non-zero timeouts.
        }

        //Shutdown Check
        //If the StringHandoff has been shutdown, if so immediately throw a ShutdownException.
        //Check for shutdown
        if (isShutdown) {
            throw new ShutdownException();
        }

        //Single Passer Check
        //Check to make sure another thread isn't already in the process of passing
        if (isPassing) {
            throw new IllegalStateException("Another thread is already passing."); //If so, throw an Illegal State Exception
        }

        //If initial checks passed, we can start passing.

        //Set passing flag
        //Mark that this thread is now passing with a flag.
        isPassing = true;

        //If there's already a message in the slot (message != null) and there isn't a shutdown,
        //release the lock until another thread calls notify() or notifyAll() (reciever picks it up)
        try {
            //wait until there is a receiver
            while (message != null && !isShutdown) {
                wait();
            }

            //Post-'wait()' Checks
            //After waking up from the wait, make sure there aren't any interruptions or shutdowns.
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            //check for shutdown after waking up
            if (isShutdown) {
                throw new ShutdownException();
            }

            //Store Message and Notify
            //store the message in the shared variable and notify all waiting threads (including potential recievers) that theres a change in state.
            message = msg;
            notifyAll();

            //Wait until message is picked up
            while(message != null && !isShutdown) {
                wait();
            }

            //Most post-'wait()' checks
            //After waking up from the wait, make sure there still aren't any interruptions or shutdowns as before.
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (isShutdown) {
                throw new ShutdownException();
            }

            //Finally, make sure the isPassing flag gets reset.
            //Happens even if there was an exception so that the system doesn't get stuck.
        } finally {
            //always reset the isPassing flag before exiting
            isPassing = false;
        }
    }

    //Provided No-Timeout Method for pass
    @Override
    public synchronized void pass(String msg) throws InterruptedException, ShutdownException, IllegalStateException {

        //Addition
        pass(msg, 0L); //timeout of (0L) = wait indefinitely
    }


    //Provided
    @Override
    public synchronized String receive(long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        //Additions

        //Timeout Check
        //For now, throw RuntimeException for non-zero timeouts
        //(Change Later)
        if (msTimeout != 0L) throw new RuntimeException("non-zero timeout is not YET supported");

        //Shutdown Check
        //Make sure StringHandoff hasn't been shutdown, if so throw a ShutdownException.
        //Check for shutdown
        if (isShutdown) {
            throw new ShutdownException();
        }

        //Recieving Check
        //Check if another thread is already receiving, if so throw IllegalStateException.
        if (isReceiving) {
            throw new IllegalStateException("Another thread is already receiving a message");
        }

        //Set Recieving flag
        //Mark that we are ready to receive
        isReceiving = true;

        try {
            //Wait until there's a message available ('message == null') and String Handoff isn't shutdown.
            while (message == null && !isShutdown) {
                wait();
            }

            //Check for interrupt or shutdown after waking up
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (isShutdown) {
                throw new ShutdownException();
            }

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


    //Provided No-Timeout Method for recieve
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