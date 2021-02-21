package it.polimi.ingsw.messages;

/**
 * Class used to send a piece of information to the clients
 */
public class InfoMessage extends SimpleMessage {
    /**
     * The data to be sent
     */
    private final String information;

    /**
     * Constructor for the class
     * @param player The recipient of the message
     * @param information The data being sent
     */
    public InfoMessage(String player, String information) {
        super(player);
        this.information = information;
    }

    /**
     * Getter for the information attribute
     * @return The data
     */
    public String getInformation() {
        return information;
    }
}
