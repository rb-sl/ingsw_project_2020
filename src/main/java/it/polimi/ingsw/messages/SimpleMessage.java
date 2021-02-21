package it.polimi.ingsw.messages;

/**
 * This class represents the simple message, from which many messages inherit
 */
public class SimpleMessage {
    /**
     * This attribute contains the nickname of player that will perform the action
     */
    private final String player;
    /**
     * This attribute contains the type of the message
     */
    private MessageType type;

    /**
     * Constructor for the class
     * @param player the nickname of the player that will perform the action
     */
    public SimpleMessage(String player) {
        this.player = player;
    }

    /**
     * Getter for the player
     * @return the value of player attribute
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Setter for the attribute type
     * @param type the type that will be set
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Getter for the attribute type
     * @return the value of the attribute type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Creates a light version of the message for non-specific clients
     * @return The light version of the message
     */
    public SimpleMessage makeLight(){
        return this;
    }
}
