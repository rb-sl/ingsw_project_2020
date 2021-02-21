package it.polimi.ingsw.model.map;

/**
 * This class is the factory method's concrete creator for the Block class
 */
public class BlockCreator extends Creator{
    /**
     * This method creates a block
     * @param level is the type of block
     * @return the map component which dynamically is a block
     */
    @Override
    public MapComponent createMapComponent(Integer level) {
        return new Block(level);
    }
}
