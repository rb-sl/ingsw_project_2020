package it.polimi.ingsw.model.god.wins;

import it.polimi.ingsw.misc.TripleState;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.god.GodWin;

/**
 * Base decorator for the win behaviour
 */
public abstract class BaseWinDecorator implements GodWin {
    /**
     * Wrapped object as per the decorator design pattern
     */
    private final GodWin wrapped;

    /**
     * Constructor for the class
     * @param wrapped The object to be decorated
     */
    public BaseWinDecorator(GodWin wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Decorator for isWinner, delegates any action to the decorated object
     * @param worker The worker checking its win conditions
     * @return True if the base conditions are met
     */
    @Override
    public TripleState isWinner(Worker worker) {
        return wrapped.isWinner(worker);
    }
}
