package it.polimi.ingsw.messages;

/**
 * This class is a simple ping message
 */
public class PingMessage {
    /**
     * This is the information of the message
     */
    private final String message;

    /**
     * Constructor for the class
     * @param message this is the information of the Ping message
     */
    public PingMessage(String message) {
        this.message = message;
    }
}
