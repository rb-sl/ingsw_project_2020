package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.misc.TripleState;

/**
 * Interface used by the God class to define the win behaviour of connected workers
 */
public interface GodWin {
    /**
     * Determines if a worker satisfies the right conditions in order to win
     * @param worker The worker checking its win conditions
     * @return A boolean value stating if the worker has won
     */
    public TripleState isWinner(Worker worker);
}
