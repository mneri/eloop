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
 * Implementations to this interface are passed to {@code EventEmitter} allowing code to be executed in response to
 * events. {@code Callback}s are always executed on the event loop thread.
 *
 * @author Massimo Neri
 * @version 1.0
 */
public interface Callback {
    void run(Object data);
}
