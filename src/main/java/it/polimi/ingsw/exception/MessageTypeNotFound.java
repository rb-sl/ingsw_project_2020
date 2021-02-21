package it.polimi.ingsw.exception;

/**
 * Exception thrown when the message type can't be found
 */
public class MessageTypeNotFound extends Exception {
    public MessageTypeNotFound(String message) {
        super("Error message: " + message);
    }
}
