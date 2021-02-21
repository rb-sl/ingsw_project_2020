package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

/**
 * Class used to read the god's information from the configuration file
 */
public class GodAttribute {
    /**
     * Enum used to know to whom the powers are applied
     */
    public enum Subject {SELF, OPPONENTS}

    /**
     * God's name
     */
    public String godName;
    /**
     * Number of times the connected workers may move
     */
    public Integer movableTimes;
    /**
     * Number of times the connected workers may build
     */
    public Integer buildableTimes;
    /**
     * List of events to which gods must be subscribed (based on subject)
     */
    public Map<Subject,List<String>> events;
    /**
     * List of movement powers to decorate the gods (based on subject)
     */
    public Map<Subject,List<String>> movements;
    /**
     * List of build powers to decorate the gods (based on subject)
     */
    public Map<Subject,List<String>> builds;
    /**
     * List of win powers to decorate the gods (based on subject)
     */
    public Map<Subject,List<String>> wins;
    /**
     * God's description
     */
    public String description;
}
