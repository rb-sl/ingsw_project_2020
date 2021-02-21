package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.exception.CantBuildException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Base decorator for the building behaviour
 */
public abstract class BaseBuildDecorator implements GodBuild {
    /**
     * Wrapped object as per the decorator design pattern
     */
    private final GodBuild wrapped;

    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public BaseBuildDecorator(GodBuild wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Decorator for canBuild, delegates the actions to the base object
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        return wrapped.canBuild(worker, coordinates);
    }

    /**
     * Decorator for buildTo (Overloaded)
     * @param worker Worker creating the block
     * @param coordinates Coordinates destination of the new build
     * @param buildType The level (1 to 4) of the new build
     * @throws CantBuildException If the worker cannot build on top of the coordinates
     * @throws WrongBlockException If the computed height is illegal
     */
    @Override
    public void buildTo(Worker worker, Coordinates coordinates, Integer buildType) throws CantBuildException, WrongBlockException {
        wrapped.buildTo(worker, coordinates, buildType);
    }
}
