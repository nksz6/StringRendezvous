package com.abc.handoff;

import com.abc.pp.stringhandoff.*;
import com.programix.thread.*;

public class StringHandoffImpl implements StringHandoff {

    public StringHandoffImpl() {
    }

    @Override
    public synchronized void pass(String msg, long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

    }

    @Override
    public synchronized void pass(String msg) throws InterruptedException, ShutdownException, IllegalStateException {
    }

    @Override
    public synchronized String receive(long msTimeout)
        throws InterruptedException, TimedOutException, ShutdownException, IllegalStateException {

        return null;
    }

    @Override
    public synchronized String receive() throws InterruptedException, ShutdownException, IllegalStateException {
        return null;
    }

    @Override
    public synchronized void shutdown() {
    }

    @Override
    public Object getLockObject() {
        return this;
    }
}