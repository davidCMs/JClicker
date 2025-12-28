package dev.davidCMs.jclicker.utils;

import dev.davidCMs.jclicker.Main;

import javax.swing.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClickerThread extends Thread {

    private final Main main;

    private boolean running = false;
    private boolean shouldPause = false;
    private boolean paused = true;

    private final Lock lock = new ReentrantLock();
    private final Condition stateChanged = lock.newCondition();

    private MouseButton button;
    private long msDelay;
    private int nsDelay;

    public ClickerThread(Main main) {
        this.main = main;
        start();
    }

    @Override
    public void run() {
        boolean clicked = false;
        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                while (shouldPause || !running) {
                    if (clicked) {
                        main.remoteDesktopManager.setMouseButton(button, false);
                        clicked = false;
                    }

                    paused = true;
                    stateChanged.signalAll();

                    stateChanged.await();
                }

                paused = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }

            try {
                main.remoteDesktopManager.setMouseButton(button, !clicked);
            } catch (Exception ignored) {}

            try {
                Thread.sleep(Math.ceilDiv(msDelay, 2), Math.ceilDiv(nsDelay, 2));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            clicked = !clicked;
        }
    }

    public void enable() {
        lock.lock();
        try {
            running = true;
            shouldPause = false;
            stateChanged.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void disable() {
        lock.lock();
        try {
            shouldPause = true;
            running = false;

            while (!paused) stateChanged.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void setButton(MouseButton button) {
        lock.lock();
        try {
            boolean wasRunning = running;

            if (wasRunning) {
                shouldPause = true;
                running = false;

                while (!paused) {
                    stateChanged.await();
                }
            }

            this.button = button;

            if (wasRunning) {
                running = true;
                shouldPause = false;
                stateChanged.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void setDelay(long ms, int ns) {
        lock.lock();
        try {
            boolean wasRunning = running;

            if (wasRunning) {
                shouldPause = true;
                running = false;

                while (!paused) {
                    stateChanged.await();
                }
            }

            this.msDelay = ms;
            this.nsDelay = ns;

            if (wasRunning) {
                running = true;
                shouldPause = false;
                stateChanged.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public MouseButton getButton() {
        lock.lock();
        try {
            return button;
        } finally {
            lock.unlock();
        }
    }

    public int getNsDelay() {
        lock.lock();
        try {
            return nsDelay;
        } finally {
            lock.unlock();
        }
    }

    public long getMsDelay() {
        lock.lock();
        try {
            return msDelay;
        } finally {
            lock.unlock();
        }
    }

    public boolean isPaused() {
        lock.lock();
        try {
            return paused;
        } finally {
            lock.unlock();
        }
    }
}
