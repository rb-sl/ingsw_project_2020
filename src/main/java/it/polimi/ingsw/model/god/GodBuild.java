package it.polimi.ingsw.model.god;

import it.polimi.ingsw.exception.CantBuildException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Interface used by the God class to define the build behaviour of connected workers
 */
public interface GodBuild {
    /**
     * Determines if a worker can build on a specified space
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list containing the buildable levels
     */
    public List<Integer> canBuild(Worker worker, Coordinates coordinates);

    /**
     * Builds a new block
     * @param worker Worker creating the block
     * @param coordinates Coordinates destination of the new build
     * @param buildType The level (1 to 4) of the new build
     * @throws CantBuildException If the worker cannot build on top of the coordinates
     * @throws WrongBlockException If the given height is illegal
     */
    public void buildTo(Worker worker, Coordinates coordinates, Integer buildType) throws WrongBlockException, CantBuildException;
}
