package it.polimi.ingsw.messages;

/**
 * This class represents the starter message from view to ChoseStarterHandler
 */
public class StarterMessage extends SimpleMessage{
    /**
     * This attribute contains the nickname of the player that will be chosen as starter
     */
    private final String starter;

    /**
     * Constructor for the class
     * @param starter the nickname of the player that will be chosen as starter
     * @param player the nickname of the player that will perform the action
     */
    public StarterMessage(String starter, String player) {
        super(player);
        this.starter = starter;
    }

    /**
     * Getter for the attribute starter
     * @return the value of the attribute starter
     */
    public String getStarter() {
        return starter;
    }
}
