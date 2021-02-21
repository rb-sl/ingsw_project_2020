package it.polimi.ingsw.misc;

/**
 * This class is used to create the Observer pattern
 * @param <T> Type of the message
 */
public interface Observer<T> {
    /**
     * Method used to update the observer
     * @param m The message being notified
     */
    public void update(T m);
}
