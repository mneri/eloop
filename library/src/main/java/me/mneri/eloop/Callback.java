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

/**
 * Implementations to this interface are passed to {@code Emitter} allowing code to be executed in response to
 * events. {@code Callback}s are always executed on the event loop thread.
 *
 * @param <T> The type of the {@code Callback} determines the type of the parameter passed to the
 *            {@link Callback#run} method.
 *
 * @author Massimo Neri
 * @version 2.0
 */
public interface Callback<T> {
    /**
     * The code to run when the event occurs.
     *
     * @param data The data passed by the event.
     */
    void run(T data);
}
