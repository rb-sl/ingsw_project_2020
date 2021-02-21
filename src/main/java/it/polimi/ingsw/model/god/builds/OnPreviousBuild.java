package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator allowing a build after the first one but restricted to the latest position
 */
public class OnPreviousBuild extends BaseBuildDecorator {
    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public OnPreviousBuild(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, allowing a new build iff on the latest build's coordinates
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list containing the buildable levels
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        if(worker.getTimesBuilt() == 0 || coordinates.equals(worker.getPlayer().getGod().getLogTurn().getLatestCoordinates()))
            return super.canBuild(worker, coordinates);
        return new ArrayList<>();
    }
}
