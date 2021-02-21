package it.polimi.ingsw.messages;

/**
 * This class is a "container" of the generic message
 */
public class MessageEnvelope {
    /**
     * This attribute contains the type of the message
     */
    private final MessageType type;
    /**
     * This attribute contains the serialized message
     */
    private final String message;

    /**
     * Constructor for the class
     * @param type the type of the message
     * @param message the serialized message
     */
    public MessageEnvelope(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Getter for the attribute type
     * @return the value of the attribute type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Getter for the attribute message
     * @return the value of the attribute message
     */
    public String getMessage() {
        return message;
    }
}
