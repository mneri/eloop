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

package me.mneri.eloop.example;

import me.mneri.eloop.Emitter;
import me.mneri.eloop.Loop;

/**
 * Example class implementing an event loop.
 *
 * @author Massimo Neri
 * @version 2.0
 */
public class HelloWorld extends Loop {
    /**
     * Entry point of the example.
     *
     * @param args Ignored.
     */
    public static void main(String[] args) {
        // Create and start a new Loop.
        new HelloWorld().start();
    }

    @Override
    protected void run() {
        // This method is invoked on loop's startup and runs on the event loop thread.

        // Get a new Emitter instance. You can emit events and add callbacks to events using an emitter instance.
        Emitter emitter = emitter();

        // Register a Callback to the 'greet' event. When the emitter emits the 'greet' event, the callback is run.
        // The on() method should be invoked only from the event loop thread. Since the code of the callback is
        // guaranteed to run on the event loop thread, you can add callbacks from there.
        emitter.on("greet", (String who) -> {
            // The following code is guaranteed to run on the event loop thread.
            System.out.println(String.format("Hello, %s!", who));
        });

        // You can register multiple callbacks to the same event. The callbacks are run in the registration order.
        emitter.on("greet", (String who) -> {
            // The following code is guaranteed to run on the event loop thread.
            System.out.println(String.format("Hello again, %s!", who));
        });

        // Register a Callback to the 'leave' event. When the emitter emits the 'greet' event, the callback is run.
        emitter.on("leave", (String who) -> {
            // The following code is guaranteed to run on the event loop thread.
            System.out.println(String.format("Goodbye, %s!", who));
            quit(); // Stop the event loop thread.
        });

        // Emit the 'greet' and 'leave' events. The type of the second parameter should match the callback's
        // parameter type. The emit method can be invoked from any thread.
        emitter.emit("greet", "world");
        emitter.emit("leave", "everyone");
    }
}
