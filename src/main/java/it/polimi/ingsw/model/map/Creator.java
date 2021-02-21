package it.polimi.ingsw.model.map;

/**
 * This is the abstract class that manages the factory method's creations
 */
public abstract class Creator {
    /**
     * This method creates the map component
     * @param level is the type of block
     * @return the new map component
     */
    public abstract MapComponent createMapComponent(Integer level);
}
