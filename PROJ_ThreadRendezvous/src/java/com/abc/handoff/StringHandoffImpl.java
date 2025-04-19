//ICS440
//Nick Kelley
//String Rendezvous Project

package com.abc.handoff;

import com.abc.pp.stringhandoff.*;
import com.programix.thread.*;

public class StringHandoffImpl implements StringHandoff {
    private String message = null;  //the message being passed
    private boolean isPassing = false; //thread passing flag
    private boolean isReceiving = false; //thread receiving flag
    private boolean isShutdown = false; //thread shutdown flag

    public StringHandoffImpl() {
    }

    @Override
    public synchronized void pass(String msg, long msTimeout) throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        if (isShutdown) {
            throw new ShutdownException();
        }
        if (isPassing) {
            throw new IllegalStateException("Another thread is already passing a message.");
        }
        isPassing = true;

        try {
            long msEndTime; //time until timeout finishes

            if (msTimeout == 0L) {
                msEndTime = Long.MAX_VALUE;
            } else {
                msEndTime = System.currentTimeMillis() + msTimeout;
            }

            while (message != null && !isShutdown) {

                if (msTimeout == 0L) {
                    wait();
                } else {
                    long msRemaining = msEndTime - System.currentTimeMillis();
                    if (msRemaining <= 0) {
                        throw new TimedOutException("timed out, no time remaining.");
                    }
                    wait(msRemaining);
                }

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                if (isShutdown) {
                    throw new ShutdownException();
                }
            }

            message = msg;
            notifyAll();
            while(message != null && !isShutdown) {
                if (msTimeout == 0L) {
                    wait();
                } else {

                    long msRemaining = msEndTime - System.currentTimeMillis();
                    if (msRemaining <= 0) {

                        throw new TimedOutException("timed out, no time remaining.");
                    }

                    wait(msRemaining);
                }


                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                if (isShutdown) {
                    throw new ShutdownException();
                }
            }

        } finally {

            if (message == msg) {
                message = null;
                notifyAll();
            }

            isPassing = false;
        }
    }

    @Override
    public synchronized void pass(String msg) throws InterruptedException, ShutdownException, IllegalStateException {
        pass(msg, 0L);
    }

    @Override
    public synchronized String receive(long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        if (isShutdown) {
            throw new ShutdownException();
        }


        if (isReceiving) {
            throw new IllegalStateException("Another thread is already receiving a message");
        }

        isReceiving = true;

        try {


            long msEndTime;
            if (msTimeout == 0L) {
                msEndTime = Long.MAX_VALUE;
            } else {
                msEndTime = System.currentTimeMillis() + msTimeout;
            }


            while (message == null && !isShutdown) {
                if (msTimeout == 0L) {
                    wait();
                } else {

                    long msTimeRemaining = (msEndTime - System.currentTimeMillis());
                    if (msTimeRemaining <= 0) {
                        throw new TimedOutException("timed out, no time remaining.");
                    }
                    wait(msTimeRemaining);
                }

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (isShutdown) {
                    throw new ShutdownException();
                }
            }

            if (isShutdown) {
                throw new ShutdownException();
            }


            String result = message;


            message = null;


            notifyAll();

            return result;

        } finally {

            isReceiving = false;
        }
    }



    @Override
    public synchronized String receive() throws InterruptedException, ShutdownException, IllegalStateException {

        return receive(0L);
    }


    @Override
    public synchronized void shutdown() {


        isShutdown = true;
        notifyAll();
    }


    @Override
    public Object getLockObject() {
        return this;
    }
}