package me.mneri.eloop;

/**
 * This exception is thrown when a {@link Callback} with generic type {@code T} is registered to an event but is
 * later invoked with {@code data} parameter of different type.
 *
 * @author Massimo Neri
 * @version 1.0
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
