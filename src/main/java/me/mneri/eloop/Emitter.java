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

import static me.mneri.eloop.Loop.Event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An {@code Emitter} is an object that fires events. Clients can register callbacks to events.
 * <p/>
 * An {@code Emitter} is bounded to an instance of {@link @Loop}. The {@link Loop} process the events
 * fired by multiple {@code Emitter}s, one at a time. The callbacks registered to an {@code Emitter} are
 * guaranteed to execute on the event loop thread.
 * <p/>
 * The API of {@code Loop} is finely tailored with the API of {@link Emitter}.
 *
 * @author Massimo Neri
 * @version 1.0
 */
public class Emitter {
    private HashMap<String, List<Callback>> mCallbacks = new HashMap<>(); // Event-callback mapping.
    private Loop mLoop; // The event loop bounded to this instance.

    /**
     * Create a new {@code Emitter} instance bounded to the specified {@link Loop}.
     *
     * @param loop The {@link Loop} to bind to.
     */
    public Emitter(Loop loop) {
        // It is not possible for a Emitter not to be bound to an Loop.
        if (loop == null)
            throw new NullPointerException("The event emitter should be bound to an actual event loop.");

        mLoop = loop;
    }

    /**
     * Add a callback to the end of the list for the specified event. No checks are made to see if the callback has
     * already been added: multiple calls with the same combination of {@code event} and {@code callback} will result
     * with the {@code callback} added multiple times.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     *
     * @param event    The event to add the callback to.
     * @param callback The callback to be added as listener to the event.
     * @return This {@code Emitter} so the calls can be chained.
     * @deprecated Use {@link Emitter#on(String, Callback)} instead.
     */
    @Deprecated
    public Emitter addListener(String event, Callback callback) {
        return on(event, callback);
    }

    /**
     * Add {@code callback} to the end of the list for the specified {@code event}s.
     * <p/>
     * As in {@link Emitter#addListener(String, Callback)} (String, Callback)}, no checks are made to see if
     * {@code callback} has already been added.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other trheads.</i>
     *
     * @param events   The events to add the callback to.
     * @param callback The callback to be added as listener to all the events.
     * @return This {@code Emitter} so the calls can be chained.
     * @deprecated Use {@link Emitter#on(String[], Callback)} instead.
     */
    @Deprecated
    public Emitter addListener(String[] events, Callback callback) {
        return on(events, callback);
    }

    /**
     * <i>This method is called internally by {@link Loop} and should not be called by a client of the library
     * .</i>
     * <p/>
     * Dispatch an event to the callbacks that previously registered.
     * <p/>
     * <i>This method is executed on the event loop thread.</i>
     *
     * @param event The event to dispatch to the registered callbacks.
     * @param data  The data to supply to the callbacks
     */
    void dispatch(String event, Object data) {
        // Get the list of callbacks registered for the event.
        List<Callback> list = mCallbacks.get(event);

        // Lists of callbacks are instantiated lazily: when the first callback is registered to an event, the list is
        // created. If no callbacks were previously registered the list for the given event is null.
        if (list != null) {
            // Run each callback.
            for (Callback callback : list)
                callback.run(data);
        }
    }

    /**
     * Call each of the callback supplied for {@code event} in order with {@code data} as argument.
     * <p/>
     * If a callback has been added multiple times with {@link Emitter#on(String, Callback)}, then it will be
     * invoked multiple times.
     * <p/>
     * <i>This method is thread-safe and can be called from background threads.</i>
     *
     * @param event The event to emit.
     * @param data  The data to supply to the callbacks.
     */
    public void emit(String event, Object data) {
        mLoop.enqueue(new Event(this, event, data));
    }

    /**
     * Remove the specified callback from the list for the specified event.
     * <p/>
     * This method will remove at most one occurrence of the listener from the list for {@code event}. If a specific
     * callback has been added multiple times for the specified event, then this method must be called multiple time to
     * remove every instance.
     * <p/>
     * <i>This method is not thread safe and should be not invoked from other threads.</i>
     *
     * @param event    The event to remove the callback from.
     * @param callback The callback to be removed.
     * @return This {@code Emitter} so calls can be chained.
     */
    public Emitter off(String event, Callback callback) {
        // Get the list of callbacks registered for the event.
        List<Callback> list = mCallbacks.get(event);

        // Lists of callbacks are instantiated lazily: when the first callback is registered to an event, the list is
        // created. If no callbacks were previously registered the list for the given event is null.
        if (list != null) {
            // Cycle through the list and remove the first occurrence of the callback.
            Iterator<Callback> iter = list.iterator();

            while (iter.hasNext()) {
                if (iter.next() == callback) {
                    iter.remove();
                    break; // Remove only the first occurrence of the callback
                }
            }
        }

        return this;
    }

    /**
     * Add {@code callback} to the end of the list for the specified {@code event}.
     * <p/>
     * No checks are made to see if {@code callback} has already been added: multiple calls with the same combination
     * of {@code event} and {@code callback} will result with the {@code callback} added multiple times.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other trheads.</i>
     *
     * @param event    The event to add the callback to.
     * @param callback The callback to be added as listener to the event.
     * @return This {@code Emitter} so the calls can be chained.
     */
    public Emitter on(String event, Callback callback) {
        // Get the list of callbacks registered for the event.
        List<Callback> list = mCallbacks.get(event);

        // Lists of callbacks are instantiated lazily: when the first callback is registered to an event, the list is
        // created. If no callbacks were previously registered the list for the given event is null.
        if (list == null) {
            list = new LinkedList<>();
            mCallbacks.put(event, list);
        }

        list.add(callback);
        return this;
    }

    /**
     * Add {@code callback} to the end of the list for the specified {@code event}s.
     * <p/>
     * As in {@link Emitter#on(String, Callback)}, no checks are made to see if {@code callback} has already been added.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other trheads.</i>
     *
     * @param events   The events to add the callback to.
     * @param callback The callback to be added as listener to all the events.
     * @return This {@code Emitter} so the calls can be chained.
     */
    public Emitter on(String[] events, Callback callback) {
        for (String event : events)
            on(event, callback);

        return this;
    }

    /**
     * Add a <i>one-time</i> callback to the end of the list for the specified {@code event}. The {@code callback} is
     * invoked only next time the event is fired, after which is removed from the list.
     * <p/>
     * No checks are made to see if {@code callback} has already been added: multiple calls with the same combination
     * of {@code event} and {@code callback} will result with the {@code callback} added multiple times.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     *
     * @param event    The event to add the callback to.
     * @param callback The callback added as listener to the event.
     * @return This {@code Emitter} so the calls can be chained.
     */
    public Emitter once(final String event, final Callback callback) {
        // Wrap the callback in another callback. The wrapping callback takes care to remove itself after it gets
        // invoked for the first time.
        return on(event, new Callback() {
            @Override
            public void run(final Object data) {
                callback.run(data);
                off(event, this);
            }
        });
    }

    /**
     * Add a <i>one-time</i> callback to the end of the lists for the specified {@code events}. The {@code callback} is
     * invoked only next time the event is fired, after which is removed from the list.
     * <p/>
     * As in {@link Emitter#once(String, Callback)} no checks are made to see if {@code callback} has already been
     * added.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     *
     * @param events   The event to add the callback to.
     * @param callback The callback added as listener to the event.
     * @return This {@code Emitter} so the calls can be chained.
     */
    public Emitter once(String[] events, Callback callback) {
        for (String event : events)
            once(event, callback);

        return this;
    }

    /**
     * Remove the specified callback from the list for the specified event.
     * <p/>
     * This method will remove at most one occurrence of the listener from the list for {@code event}. If a specific
     * callback has been added multiple times for the specified event, then this method must be called multiple time to
     * remove every instance.
     * <p/>
     * <i>This method is not thread safe and should not be invoked from other threads.</i>
     *
     * @param event    The event to remove the callback from.
     * @param callback The callback to be removed.
     * @return This {@code Emitter} so calls can be chained.
     * @deprecated Use {@link Emitter#off(String, Callback)} instead.
     */
    @Deprecated
    public Emitter removeListener(String event, Callback callback) {
        return off(event, callback);
    }
}
