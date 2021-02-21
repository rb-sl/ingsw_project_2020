package it.polimi.ingsw.model.god;

/**
 * Class used to notify a trigger to a god, as per the observer pattern
 */
public class GodTrigger {
    /**
     * The event being notified
     */
    private final String event;
    /**
     * The associated data
     */
    private final Integer data;

    /**
     * Constructor for the class
     * @param event The event to be notified
     * @param data The connected data
     */
    public GodTrigger(String event, Integer data) {
        this.event = event;
        this.data = data;
    }

    /**
     * Getter for the event
     * @return The event name
     */
    public String getEvent() {
        return event;
    }

    /**
     * Getter for the data
     * @return The data
     */
    public Integer getData() {
        return data;
    }
}
