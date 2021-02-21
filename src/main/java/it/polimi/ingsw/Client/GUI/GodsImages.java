package it.polimi.ingsw.Client.GUI;

/**
 * Class used to deserialize the file containing the relative paths to each God images
 */
public class GodsImages {
    /**
     * Relative path to the full version of the image
     */
    private String full;
    /**
     * Relative path to the small version of the image
     */
    private String small;

    /**
     * Getter for the relative path to the full image of the God
     * @return Relative path to the full God image
     */
    public String getFull() {
        return full;
    }

    /**
     * Getter for the relative path to the small image of the God
     * @return Relative path to the small God image
     */
    public String getSmall() {
        return small;
    }
}
