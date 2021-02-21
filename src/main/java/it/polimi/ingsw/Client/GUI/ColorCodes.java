package it.polimi.ingsw.Client.GUI;

/**
 * Class used to deserialize the file containing colors and images of the workers available in GUI.
 */
public class ColorCodes {
    /**
     * Color of the worker
     */
    private String color;
    /**
     * Relative path to female worker image of this color
     */
    private String female;
    /**
     * Relative path to male worker image of this color
     */
    private String male;

    /**
     * Getter for the color of this worker
     * @return Worker color
     */
    public String getColor() {
        return color;
    }

    /**
     * Getter for the relative path to the female worker image
     * @return Relative path of female worker image
     */
    public String getFemale() {
        return female;
    }

    /**
     * Getter for the relative path to the male worker image
     * @return Relative path of male worker image
     */
    public String getMale() {
        return male;
    }
}
