package it.polimi.ingsw.model.god.wins;

import it.polimi.ingsw.misc.TripleState;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodWin;

/**
 * Class used as base win behaviour
 */
public class BaseWin implements GodWin {
    /**
     * Checks if a worker has won
     * @param worker The worker checking its win conditions
     * @return True if the win conditions are met
     */
    @Override
    public TripleState isWinner(Worker worker) {
        if (worker.getCurrentPosition().getHeight() == 3 && worker.getPreviousPosition().getHeight() == 2) {
            return TripleState.TRUE;
        } else {
            return TripleState.INDIFFERENT;
        }
    }
}
