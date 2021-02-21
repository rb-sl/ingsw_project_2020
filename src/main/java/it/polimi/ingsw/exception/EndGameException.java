package it.polimi.ingsw.exception;

/**
 * Exception thrown when a game ends
 */
public class EndGameException extends Exception {
    public EndGameException(String message) {
        super(message);
    }
}
