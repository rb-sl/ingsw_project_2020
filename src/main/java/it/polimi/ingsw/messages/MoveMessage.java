package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.map.SimpleCoordinates;

/**
 * This class represents the godInit message from view to moveHandler
 */
public class MoveMessage extends SimpleMessage{
    /**
     * Coordinates of the worker that will move
     */
    private final SimpleCoordinates source;
    /**
     * Coordinates of the place where the worker will move
     */
    private final SimpleCoordinates destination;
    /**
     * Constructor for the class
     * @param source coordinates of the worker that will move
     * @param destination coordinates of the place where the worker will be moved
     * @param player the nickname of the player that will perform the move
     */
    public MoveMessage(SimpleCoordinates source, SimpleCoordinates destination, String player) {
        super(player);
        this.source = source;
        this.destination = destination;
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
}
