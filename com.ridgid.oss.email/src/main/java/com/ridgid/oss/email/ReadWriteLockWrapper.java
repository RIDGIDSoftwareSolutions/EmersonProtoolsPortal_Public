package com.ridgid.oss.email;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

class ReadWriteLockWrapper {
    private final ReadWriteLock lock;

    public ReadWriteLockWrapper(ReadWriteLock lock) {
        this.lock = lock;
    }

    void doInReadLock(Runnable action) {
        doInLock(lock.readLock(), action);
    }

    void doInWriteLock(Runnable action) {
        doInLock(lock.writeLock(), action);
    }

    private void doInLock(Lock lock, Runnable action) {
        try {
            lock.lock();
            action.run();
        } finally {
            lock.unlock();
        }
    }
}
