package it.polimi.ingsw.misc;
/**
 * This class is used to create the pattern Observer over two parameters
 * @param <T> first parameter
 * @param <V> second parameter
 */
public interface BiObserver<T, V> {
    /**
     * Update function as per the observer pattern
     * @param m The first message being notified
     * @param v The second message being notified
     */
    public void update(T m, V v);
}
