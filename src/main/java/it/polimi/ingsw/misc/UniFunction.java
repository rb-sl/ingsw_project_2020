package it.polimi.ingsw.misc;

/**
 * Interface to parametrize a function with a single parameter and no return
 * @param <M> Type of the parameter passed to this function
 */
public interface UniFunction<M> {
    void allocateMessage(M message);
}
