package it.polimi.ingsw.model.map;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.exception.NotFreeException;
import it.polimi.ingsw.model.Worker;
import org.junit.jupiter.api.Test;

/**
 * Tests for the block class
 */
public class BlockTest {
    Block block = new Block(0);

    /**
     * Test for is free method
     */
    @Test
    public void isFreeTest() {
        assertTrue(block.isFree());
        block.setTop(0);
        assertTrue(!block.isFree());
        try {
            block.setWorkerOnTop(new Worker(null, 'F'));
        }catch(NotFreeException ex){
            assertTrue(true);
        }
        assertTrue(!block.isFree());
        block.setTop(1);
        assertTrue(!block.isFree());

    }

    /**
     * Test for hasWorkerOnTop method
     */
    @Test
    public void hasWorkerOnTopTest() {
        try {
            block.setWorkerOnTop(new Worker(null, 'F'));
        }catch(NotFreeException ex){
            assertTrue(true);
        }
        assertTrue(block.hasWorkerOnTop());
        block.removeWorkerOnTop();
        assertTrue(!block.hasWorkerOnTop());

    }

    /**
     * Test for isTopTest method
     */
    @Test
    public void isTopTest() {
        assertTrue(block.isTop());
        block.setTop(0);
        assertTrue(!block.isTop());
    }

    /**
     * Test for setWorkerOnTopTest
     */
    @Test
    public void setWorkerOnTopTest() {
        try {
            block.setWorkerOnTop(new Worker(null, 'F'));
        } catch (NotFreeException ex) {
            assertTrue(true);
        }
        try {
            block.setWorkerOnTop(new Worker(null, 'F'));
        } catch (NotFreeException ex) {
            assertTrue(true);
        }
        block.removeWorkerOnTop();
    }

    /**
     * Test for removeWorkerOnTopTest
     */
    @Test
    public void removeWorkerOnTopTest(){
        block.removeWorkerOnTop();
        assertTrue(!block.hasWorkerOnTop());
    }
}