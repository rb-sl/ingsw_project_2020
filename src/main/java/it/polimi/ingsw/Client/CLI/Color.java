package it.polimi.ingsw.Client.CLI;

/**
 * Enum for cli colors
 */
public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_PURPLE("\u001B[35m");
    static final String RESET = "\u001B[0m";

    /**
     * Color of the variable
     */
    private final String color;

    /**
     * Constructor for the class
     * @param color The color to set
     */
    Color(String color) {
        this.color = color;
    }

    /**
     * Getter for the color attribute
     * @return The color
     */
    public String getColor() {
        return this.color;
    }
}
