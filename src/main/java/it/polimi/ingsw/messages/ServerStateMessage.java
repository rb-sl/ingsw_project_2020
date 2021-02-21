package it.polimi.ingsw.messages;

/**
 * Class representing the message sent to a client wishing to connect
 */
public class ServerStateMessage {
    /**
     * Attribute stating if the server can accept the client
     */
    private final boolean open;
    /**
     * Attribute specifying if there is an active game waiting for players
     */
    private final boolean active;

    /**
     * Constructor for the class
     * @param open The open attribute
     * @param active The active attribute
     */
    public ServerStateMessage(boolean open, boolean active) {
        this.open = open;
        this.active = active;
    }

    /**
     * Getter for the open attribute
     * @return True if the server is open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Getter for the active attribute
     * @return True if there is an active game
     */
    public boolean isActive() {
        return active;
    }
}
