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
 * An {@code EventLoop} is an active class that enqueues and dispatches events. The loop runs in the so called event
 * loop thread.
 * <p/>
 * This class is strictly coupled with {@link EventEmitter}. Emitters expose events and collect callbacks. When an
 * event is fired it is delivered to {@code EventLoop}. {@code EventLoop} takes care that callbacks are always
 * executed on the event loop thread.
 *
 * @author Massimo Neri
 * @version 1.0
 */
public abstract class EventLoop {
    static class Event {
        public final Object data;
        public final EventEmitter emitter;
        public final String name;

        Event(EventEmitter emitter, String name, Object data) {
            this.emitter = emitter;
            this.name = name;
            this.data = data;
        }
    }

    private BlockingQueue<Event> mEventQueue = new LinkedBlockingQueue<>(); // The queue of events.
    private volatile boolean mRunning; // true if the loop is running.

    /**
     * <i>This method is called internally by {@link EventEmitter} and should not be called by a client of the library
     * in any way.</i>
     * <p/>
     * Enqueue an event in the event queue. The event will be eventually processed and its callback executed.
     * <p/>
     * <i>This method is thread safe. It is called internally by {@link EventEmitter} and probably executed on a
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
     * Stop the event loop.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     */
    public void quit() {
        mRunning = false;
    }

    /**
     * When a {@code EventLoop} is started, the run method is called in the event loop thread. The client can fire
     * events using an {@link EventEmitter} instance.
     */
    protected abstract void run();

    /**
     * Start the event loop. Upon starting the method {@link EventLoop#run()} is invoked and executed on the event
     * loop thread.
     */
    public void start() {
        // This is the event loop thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRunning = true;
                run();

                while (mRunning) {
                    try {
                        // The event queue is thread safe. Usually, events are put in the queue by background threads
                        // (through the method {@link EventLoop#enqueue} and taken by this thread, the event loop
                        // thread.
                        Event event = mEventQueue.take();
                        // Dispatch the event to the emitter. Since the call to this method is made by the event loop
                        // thread, all the code executed in the callback runs on the event loop thread.
                        event.emitter.dispatch(event.name, event.data);
                    } catch (InterruptedException ignored) {
                        // We should ignore this exception since we want {@link EventLoop#quit()} be the only way to
                        // stop and event loop.
                    }
                }
            }
        }).start();
    }
}
