package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Decorator class preventing the construction of a dome on the second build
 */
public class NoDomeOnSecond extends BaseBuildDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The object to be decorated
     */
    public NoDomeOnSecond(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, preventing the construction of domes if on second build
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        List<Integer> levelList = super.canBuild(worker, coordinates);
        if(worker.getTimesBuilt() != 0)
            levelList.remove(Integer.valueOf(4));

        return levelList;
    }
}
