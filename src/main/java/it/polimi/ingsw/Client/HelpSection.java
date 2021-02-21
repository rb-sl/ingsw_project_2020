package it.polimi.ingsw.Client;

import java.util.List;

/**
 * Class used to retrieve information from the rules configuration file
 */
public class HelpSection {
    /**
     * Name of the section
     */
    String section;
    /**
     * The text of the section
     */
    String text;
    /**
     * Optional subsections
     */
    List<HelpSection> subsections;

    /**
     * Getter for the section attribute
     * @return The section's name
     */
    public String getSection() {
        return section;
    }

    /**
     * Getter for the text attribute
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * Getter for the subsection list
     * @return The subsection list
     */
    public List<HelpSection> getSubSection() {
        return subsections;
    }
}


