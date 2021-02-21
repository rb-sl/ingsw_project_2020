package it.polimi.ingsw.server;

import java.util.TimerTask;

/**
 * This class is the task done after the timeout of the timer in the client socket connection, that checks if the connection is alive or not
 */
public class TimerTimeout extends TimerTask {
    /**
     * The connection that creates the timer
     */
    private final ClientSocketConnection c;

    /**
     * Constructor of the class
     * @param c the connection that create the timer
     */
    public TimerTimeout(ClientSocketConnection c) {
        this.c = c;
    }

    /**
     * This is the method that is run after the timeout, it sets the attribute dead of the connection to true
     */
    @Override
    public void run() {
        c.setDead(true);
    }
}
