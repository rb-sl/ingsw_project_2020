package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.List;

/**
 * Decorator Class allowing the build of domes on any buildable block
 */
public class DomeEverywhere extends BaseBuildDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public DomeEverywhere(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, creating the possibility to build a dome at any level
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        List<Integer> levelList = super.canBuild(worker, coordinates);

        if(!levelList.isEmpty() && !levelList.contains(4)) {
            levelList.add(4);
        }

        return levelList;
    }
}

