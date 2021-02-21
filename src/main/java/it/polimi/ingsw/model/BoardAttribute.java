package it.polimi.ingsw.model;

import java.util.List;

/**
 * Class used to read board attributes from the configuration files
 */
public class BoardAttribute {
    /**
     * Number of rows to create
     */
    public Integer nRow;
    /**
     * Number of columns to create
     */
    public Integer nCol;
    /**
     * Number of workers available to each worker
     */
    public Integer workersPerPlayer;
    /**
     * List of worker identifiers
     */
    public List<Character> workerSex;
}
