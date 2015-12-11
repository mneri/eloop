package me.mneri.eloop.example;

import me.mneri.eloop.Emitter;
import me.mneri.eloop.Loop;

public class HelloWorld extends Loop {
    public static void main(String[] args) {
        new HelloWorld().start();
    }

    @Override
    protected void run() {
        Emitter greeter = emitter();
        greeter.on("greet", (String who) -> {
            System.out.println(String.format("Hello, %s!", who));
        });
        greeter.emit("greet", "world");
    }
}
