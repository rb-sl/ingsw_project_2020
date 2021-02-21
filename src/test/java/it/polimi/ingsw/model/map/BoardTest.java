package it.polimi.ingsw.model.map;

import it.polimi.ingsw.exception.WrongBlockException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.god.God;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the board
 */
public class BoardTest {
    /**
     * Method to test if the player is removed correctly from the list in board class
     */
    @Test
    public void removePlayerTest() {
        Board board = new Board(5,5, 2);
        Player p = new Player("Donald duck", board);
        List<Player> players = new ArrayList<Player>();
        players.add(new Player("Uncle Scrooge", board));
        players.add(new Player("Donald duck", board));
        players.add(new Player("Mickey Mouse", board));
        board.addPlayers(players);
        p.setGod(new God("name", p));
        board.removePlayer(p);
        assertTrue(board.getPlayersList().size() == 2);
    }

    /**
     * Method to check whether the list of players returned from board objects is copied correctly
     * and every change made to this list are not applied to the one in board (the original one)
     */
    @Test
    public void getPlayerListTest() {
        Board board = new Board(5,5, 2);
        List<Player> players = new ArrayList<Player>();
        players.add(new Player("Uncle Scrooge", board));
        players.add(new Player("Donald duck", board));
        players.add(new Player("Mickey Mouse", board));
        board.addPlayers(players);
        players.remove(new Player("Donal duck", board));
        List<Player> copyed = board.getPlayersList();
        copyed.remove(2);
        assertFalse(copyed.size() == board.getPlayersList().size());
    }

    /**
     * Method to test if the level returned is correct
     */
    @Test
    public void getTopBlockTest() {
        Board board = new Board(5,5, 2);
        Coordinates cr_22 = new Coordinates(2,2, board);
        Coordinates cr_23 = new Coordinates(2,3, board);
        try {
            board.addMapComponent(cr_22, 1);
            board.addMapComponent(cr_23, 4);
            assertTrue(board.getTopBlock(cr_22).getLevel() == 1);
            assertTrue(board.getTopBlock(cr_23).getLevel() == 4);
        }
        catch(WrongBlockException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to check if the height returned from getHeight is correct
     */
    @Test
    public void getHeightTest() {
        Board board = new Board(5,5, 2);
        Coordinates cr = new Coordinates(2,2, board);
        try {
            board.addMapComponent(cr, 1);
            board.addMapComponent(cr, 2);
            assertTrue(board.getHeight(cr) == 2);
            board.addMapComponent(cr, 3);
            assertTrue(board.getHeight(cr) == 3);
        }
        catch(WrongBlockException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to test if addMapComponents works correctly and assures that if there is a wrong level
     * passed to this method, an exception is thrown
     */
    @Test
    public void addMapComponentTest() {
        Board board = new Board(5, 5, 2);
        Coordinates cr = new Coordinates(2, 2, board);
        try {
            board.addMapComponent(cr, 1);
            board.addMapComponent(cr, 2);
            assertTrue(board.getTopBlock(cr).getLevel() == 2);
            assertThrows(WrongBlockException.class, () -> board.addMapComponent(cr, 5));
            assertThrows(WrongBlockException.class, () -> board.addMapComponent(cr, 1));
            board.addMapComponent(cr, 4);
            assertTrue(board.getTopBlock(cr).getLevel() == 4);
        } catch (WrongBlockException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to test if the block is removed correctly from his stack
     */
    @Test
    public void removeTopBlock() {
        Board board = new Board(5, 5, 2);
        Coordinates cr = new Coordinates(2, 2, board);
        try {
            board.addMapComponent(cr, 1);
            board.addMapComponent(cr, 2);
            board.removeTopBlock(cr);
            assertTrue(cr.getTopBlock().getLevel() == 1);
        }
        catch (WrongBlockException exception) {
            exception.printStackTrace();
        }
    }

    /**
     *  Method to test that the list of opponents returned does not contain the worker
     *  calling getOpponents and that this list changes doesn't affect the list in board
     */
    @Test
    public void getOpponentsTest() {
        Board board = new Board(5, 5, 2);
        Player p1 = new Player("Donald duck", board);
        Player p2 = new Player("Uncle Scrooge", board);
        Player p3 = new Player("Mickey Mouse", board);
        board.addPlayers(p1);
        board.addPlayers(p2);
        board.addPlayers(p3);
        List<Player> opponents = p1.getOpponents();
        assertTrue(opponents.contains(p2));
        assertTrue(opponents.contains(p3));
        assertFalse(opponents.contains(p1));
        assertTrue(opponents.size() == 2);
        opponents.remove(p2);
        assertFalse(opponents.size() == p1.getOpponents().size());
    }
}
