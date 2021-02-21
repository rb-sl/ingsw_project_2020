package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator class used to check that a build does not prevent the ability to move afterwards
 */
public class CanBuildAndMove extends BaseBuildDecorator {
    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public CanBuildAndMove(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, checks that a worker can still move after the build
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of buildable levels that do not prevent a move
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        List<Integer> levelList = super.canBuild(worker, coordinates);

        if(worker.getTimesMoved() == 0) {
            for (Integer level : new ArrayList<>(levelList)) {
                // For all levels that can be built checks that the worker can still move
                try {
                    // Hypothesizes a build
                    coordinates.getBoard().addMapComponent(coordinates, level);

                    if (worker.reachableCells().isEmpty())
                        levelList.remove(level);

                    // Reverts to the original state
                    coordinates.getBoard().removeTopBlock(coordinates);
                } catch (WrongBlockException e) {
                    e.printStackTrace();
                }
            }
        }
        return levelList;
    }
}
