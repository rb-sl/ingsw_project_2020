package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.map.SimpleCoordinates;

/**
 * This class represents the build message from view to BuildHandler
 */
public class BuildMessage extends SimpleMessage{
    /**
     * Coordinates of the worker that will build
     */
    private final SimpleCoordinates source;
    /**
     * Coordinates of the place that will be built
     */
    private final SimpleCoordinates destination;
    /**
     * Level of the build
     */
    private final Integer level;

    /**
     * Constructor for the class
     * @param source coordinates of the worker that will build
     * @param destination coordinates of the place that will be built
     * @param level level of the build
     * @param player the nickname of the player that will perform the build
     */
    public BuildMessage(SimpleCoordinates source, SimpleCoordinates destination, Integer level, String player) {
        super(player);
        this.source = source;
        this.destination = destination;
        this.level = level;
    }

    /**
     * Getter for the attribute source
     * @return the value of the attribute source
     */
    public SimpleCoordinates getSource() {
        return source;
    }

    /**
     * Getter for the attribute destination
     * @return the value of the attribute destination
     */
    public SimpleCoordinates getDestination() {
        return destination;
    }

    /**
     * Getter for the attribute level
     * @return the value of the attribute level
     */
    public Integer getLevel() {
        return level;
    }
}
