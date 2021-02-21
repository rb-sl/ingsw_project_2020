package it.polimi.ingsw.model.map;

/**
 * This class is the factory method's concrete creator for the Dome class
 */
public class DomeCreator extends Creator {
    /**
     * This method creates a dome
     * @param level is the type of block
     * @return the new map component which dynamically is a dome
     */
    @Override
    public MapComponent createMapComponent(Integer level) {
        return new Dome();
    }
}
