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
 * Thrown to indicate that an event loop is not in an appropriate state for the requested operation. See, for example,
 * the {@link Loop#start()} and  {@link Loop#quit()} methods.
 *
 * @author Massimo Neri
 * @version 1.0
 */
public class IllegalLoopStateException extends RuntimeException {
}
