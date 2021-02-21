package it.polimi.ingsw.model.god.builds;

import it.polimi.ingsw.exception.CantBuildException;
import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodBuild;
import it.polimi.ingsw.model.map.Coordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator class to allow a worker to build before moving
 */
public class BuildBeforeMove extends BaseBuildDecorator {
    private static final String eventName = "CantMoveUp";
    private static final String eventBefore = "Before";

    /**
     * Constructor for the decorator
     * @param wrapped The decorated object
     */
    public BuildBeforeMove(GodBuild wrapped) {
        super(wrapped);
    }

    /**
     * Overridden canBuild, allowing the build once if not moved, plus the normal build
     * @param worker Worker trying to build
     * @param coordinates Coordinates destination of the new build
     * @return A list of Integers representing the buildable heights
     */
    @Override
    public List<Integer> canBuild(Worker worker, Coordinates coordinates) {
        List<Integer> levelList = new ArrayList<>();

        if (worker.getTimesMoved() == 0) {
            if (!worker.getPlayer().getGod().isTriggered(eventBefore)) {
                worker.setTimesMoved(1);
                levelList.addAll(super.canBuild(worker, coordinates));
                worker.setTimesMoved(0);
            }
        }
        else {
            if(!worker.getPlayer().getGod().isTriggered(eventBefore)) {
                if(worker.getTimesBuilt() < 1) {
                    levelList.addAll(super.canBuild(worker, coordinates));
                }
            }
            else {
                levelList.addAll(super.canBuild(worker, coordinates));
            }
        }
        return levelList;
    }

    /**
     * Overridden buildTo, triggering CantMoveUp
     * @param worker Worker creating the block
     * @param coordinates Coordinates destination of the new build
     * @throws CantBuildException If the worker cannot build on top of the coordinates
     * @throws WrongBlockException If the computed height is illegal
     */
    @Override
    public void buildTo(Worker worker, Coordinates coordinates, Integer buildType) throws CantBuildException, WrongBlockException {
        super.buildTo(worker, coordinates, buildType);

        if(worker.getTimesMoved() == 0) {
            worker.getPlayer().getGod().getEventManager().notifyTrigger(eventName, 1);
            worker.getPlayer().getGod().getEventManager().notifyTrigger(eventBefore, 1);
        }
    }
}
