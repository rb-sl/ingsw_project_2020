package it.polimi.ingsw.model.map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for creator class
 */
public class CreatorTest {
    private Creator domeCreator, blockCreator;
    private Integer level;
    private Board board = new Board(5, 5, 2);

    /**
     * Test for the creations of the different types of map component
     */
    @Test
    public void creatorTest(){
        level = 0;
        blockCreator = new BlockCreator();
        assertTrue(blockCreator.createMapComponent(level).canBuildOnTop());
        level = 1;
        assertTrue(blockCreator.createMapComponent(level).canBuildOnTop());
        level = 2;
        assertTrue(blockCreator.createMapComponent(level).canBuildOnTop());
        level = 3;
        assertTrue(blockCreator.createMapComponent(level).canBuildOnTop());
        level = 4;
        domeCreator = new DomeCreator();
        assertTrue(!domeCreator.createMapComponent(level).canBuildOnTop());
    }
}
