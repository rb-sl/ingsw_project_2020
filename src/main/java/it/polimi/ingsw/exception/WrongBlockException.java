package it.polimi.ingsw.exception;

/**
 * Exception thrown when a worker tries to build the wrong level
 */
public class WrongBlockException extends Exception{
    public WrongBlockException(String message) {
        super("Wrong block at level: " + message);
    }
}
