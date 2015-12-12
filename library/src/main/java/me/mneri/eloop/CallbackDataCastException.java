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
 * This exception is thrown when a {@link Callback} with generic type {@code T} is registered to an event but is
 * later invoked with {@code data} parameter of different type.
 *
 * @author Massimo Neri
 * @version 2.0
 */
public class CallbackDataCastException extends RuntimeException {
    /**
     * Create a new instance of this exception with the specified {@code event} and {@code cause}.
     *
     * @param event The event that was dispatched when the exception arose.
     * @param cause The {@link ClassCastException} that caused this exception.
     */
    public CallbackDataCastException(String event, ClassCastException cause) {
        super("For event '" + event + "': " + cause.getLocalizedMessage());
    }
}
