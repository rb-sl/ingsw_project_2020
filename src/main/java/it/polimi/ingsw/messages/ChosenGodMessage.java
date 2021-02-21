package it.polimi.ingsw.messages;

/**
 * Class representing the message containing the chosen god
 */
public class ChosenGodMessage {
    /**
     * The god's name
     */
    private final String name;
    /**
     * The god's description
     */
    private final String description;

    /**
     * Constructor for the class
     * @param name The god's name
     * @param description The god's description
     */
    public ChosenGodMessage(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for the name attribute
     * @return The god's name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the description attribute
     * @return The god's description
     */
    public String getDescription() {
        return description;
    }
}
