package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator class preventing the build of a second block, if on perimeter
 */
public class NoPerimeterOnSecond extends BaseBuildDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The object to be decorated
     */
    public NoPerimeterOnSecond(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, does not allow the action if the second build is on a perimeter space
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        if(worker.getTimesBuilt() == 0 || !coordinates.isOnPerimeter()) {
            return super.canBuild(worker, coordinates);
        }

        return new ArrayList<>();
    }
}
