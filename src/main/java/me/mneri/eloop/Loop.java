/*
 * This file is part of eloop.
 *
 * eloop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eloop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eloop. If not, see <http://www.gnu.org/licenses/>.
 */

package me.mneri.eloop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An {@code Loop} is an active class that enqueues and dispatches events. The loop runs in the so called event
 * loop thread.
 * <p/>
 * This class is strictly coupled with {@link Emitter}. Emitters expose events and collect callbacks. When an
 * event is fired it is delivered to {@code Loop}. {@code Loop} takes care that callbacks are always
 * executed on the event loop thread.
 *
 * @author Massimo Neri
 * @version 1.0
 */
public abstract class Loop {
    static class Event {
        public final Object data;
        public final Emitter emitter;
        public final String name;

        Event(Emitter emitter, String name, Object data) {
            this.emitter = emitter;
            this.name = name;
            this.data = data;
        }
    }

    private BlockingQueue<Event> mEventQueue = new LinkedBlockingQueue<>(); // The queue of events.
    private volatile boolean mRunning; // true if the loop is running.
    private Thread mThread = new Thread(new Runnable() { // This is the event loop thread.
        @Override
        public void run() {
            mRunning = true;
            Loop.this.run();

            while (mRunning) {
                try {
                    // The event queue is thread safe. Usually, events are put in the queue by background threads
                    // (through the method {@link Loop#enqueue} and taken by this thread, the event loop
                    // thread.
                    Event event = mEventQueue.take();
                    // Dispatch the event to the emitter. Since the call to this method is made by the event loop
                    // thread, all the code executed in the callback runs on the event loop thread.
                    event.emitter.dispatch(event.name, event.data);
                } catch (InterruptedException ignored) {
                    // We should ignore this exception since we want {@link Loop#quit()} be the only way to
                    // stop and event loop.
                }
            }
        }
    });

    /**
     * <i>This method is called internally by {@link Emitter} and should not be called by a client of the library
     * in any way.</i>
     * <p/>
     * Enqueue an event in the event queue. The event will be eventually processed and its callback executed.
     * <p/>
     * <i>This method is thread safe. It is called internally by {@link Emitter} and probably executed on a
     * background thread.</i>
     *
     * @param event The event to enqueue in the event queue.
     */
    void enqueue(Event event) {
        // The event queue is thread safe: multiple threads can add and retrieve its elements. Usually, events are
        // put in the queue by background threads and are taken by the event loop thread.
        mEventQueue.add(event);
    }

    /**
     * Return {@code true} if the current thread of execution is the event loop thread.
     *
     * @return {@code true} if the current execution thread is the event loop thread, {@code false} otherwise.
     */
    boolean isEventLoopThread(Thread thread) {
        return mThread == thread;
    }

    /**
     * Stop the event loop.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     */
    public void quit() {
        mRunning = false;
    }

    /**
     * When a {@code Loop} is started, the run method is called in the event loop thread. The client can fire
     * events using an {@link Emitter} instance.
     */
    protected abstract void run();

    /**
     * Start the event loop. Upon starting the method {@link Loop#run()} is invoked and executed on the event
     * loop thread.
     */
    public void start() {
        mThread.start();
    }
}
