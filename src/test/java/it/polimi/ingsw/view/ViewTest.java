package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.server.LocalGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class that simulates a simple game with 2 players and no god to test the MVC pattern
 */
class ViewTest {
    /**
     * Local game instance where there is the game
     */
    LocalGame game = new LocalGame();

    /**
     * This test simulates a 2 players game without god to test the interactions between model-view-controller
     */
    @Test
    void handleMessage() {
        game.run();
        Model model = game.getModel();
        //verifies that there are 2 players in the game
        assertTrue(game.getNames().size()==2);
        //game execution with player 1 challenger
        if(game.getChallenger().equals(game.getNames().get(0))) {
            //God inititializations, atheist means that there is no god for the player
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            //God inititializations, atheist means that there is no god for the player
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            //the challenger chose the starter player
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            //the first player try to place his worker but he fails because he try to do something in the opponent turn
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            //the second player place and initialize the first worker
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the second player try to place his second worker but he fails because the cell is occupied
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            //the second player place and initialize his second worker
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            //the second player pass the turn
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            //the first player place and initialize his first worker
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            //the first player place and initialize his second worker
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the firs player pass the turn
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            //the second player perform the move
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Alberto}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the second player perform the build
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 1, 'column': 3}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            //the second player pass the turn
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            //the first player perform the move
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 3, 'column': 2}, 'destination': {'row': 2, 'column': 2}, 'player': Davide}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the first player perform the build
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 1, 'column': 3}, 'level': 2, 'player': Davide}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            //the first player pass the turn
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 0, 'column': 2}, 'player': Alberto}\"}");
            c = new Coordinates(0, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 2}, 'destination': {'row': 1, 'column': 2}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'player': Davide}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 2, 'column': 2}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 0, 'column': 2}, 'destination': {'row': 1, 'column': 1}, 'player': Alberto}\"}");
            c = new Coordinates(1, 1,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 1}, 'destination': {'row': 1, 'column': 2}, 'level': 2, 'player': Alberto}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 1, 'column': 4}, 'player': Davide}\"}");
            c = new Coordinates(1, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 4}, 'destination': {'row': 1, 'column': 3}, 'level': 3, 'player': Davide}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==3);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 1}, 'destination': {'row': 2, 'column': 2}, 'player': Alberto}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 4}, 'destination': {'row': 2, 'column': 3}, 'player': Davide}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 1, 'column': 4}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(1, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message':\"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 1, 'column': 2}, 'player': Alberto}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 2}, 'destination': {'row': 2, 'column': 2}, 'level': 2, 'player': Alberto}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 2, 'column': 2}, 'player': Davide}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'level': 2, 'player': Davide}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 2}, 'destination': {'row': 1, 'column': 3}, 'player': Alberto}\"}");
        }else//game execution with player 2 challenger
        {
            //God inititializations, atheist means that there is no god for the player
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            //God inititializations, atheist means that there is no god for the player
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            //the first player try to chose the starter player but he fails because he isn't the challenger
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            //the challenger chose the starter player
            game.runCommand("2/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Davide,'player': Alberto}\"}");
            //the first player place and initialize his first worker
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            //the first player try to place his worker but he fails because he try to do something in the opponent turn
            game.runCommand("2/{'type': \"WORKERINIT\", 'message':\"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the first player try to place his second worker but he fails because the cell is occupied
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            //the first player place and initialize his second worker
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            //the first player pass the turn
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            //the second player place and initialize his first worker
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            //the second player place and initialize his second worker
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            //check the presence of the worker, the owner and the sex
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the second player pass the turn
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            //the first player perform the move
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Davide}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the first player perform the build
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 1, 'column': 3}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            //the first player pass the turn
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            //the second player perform the move
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 3, 'column': 2}, 'destination': {'row': 2, 'column': 2}, 'player': Alberto}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the second player perform the build
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 1, 'column': 3}, 'level': 2, 'player': Alberto}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            //the second player pass the turn
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 0, 'column': 2}, 'player': Davide}\"}");
            c = new Coordinates(0, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 2}, 'destination': {'row': 1, 'column': 2}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'player': Alberto}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 2, 'column': 2}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 0, 'column': 2}, 'destination': {'row': 1, 'column': 1}, 'player': Davide}\"}");
            c = new Coordinates(1, 1,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 1}, 'destination': {'row': 1, 'column': 2}, 'level': 2, 'player': Davide}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 1, 'column': 4}, 'player': Alberto}\"}");
            c = new Coordinates(1, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 4}, 'destination': {'row': 1, 'column': 3}, 'level': 3, 'player': Alberto}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==3);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 1}, 'destination': {'row': 2, 'column': 2}, 'player': Davide}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 4}, 'destination': {'row': 2, 'column': 3}, 'player': Alberto}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 1, 'column': 4}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(1, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 1, 'column': 2}, 'player': Davide}\"}");
            c = new Coordinates(1, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 1, 'column': 2}, 'destination': {'row': 2, 'column': 2}, 'level': 2, 'player': Davide}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 2, 'column': 3}, 'destination': {'row': 2, 'column': 2}, 'player': Alberto}\"}");
            c = new Coordinates(2, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 2, 'column': 2}, 'destination': {'row': 2, 'column': 3}, 'level': 2, 'player': Alberto}\"}");
            c = new Coordinates(2, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==2);
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 2}, 'destination': {'row': 1, 'column': 3}, 'player': Davide}\"}");
        }
    }

    /**
     * This test tests the interactions between model-view-controller after a lose command
     */
    @Test
    public void loseTest(){
        game.run();
        Model model = game.getModel();
        assertTrue(game.getNames().size()==2);
        if(game.getChallenger().equals(game.getNames().get(0))){
            //initialization
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            //lose command
            game.runCommand("2/{'type': \"LOSE\", 'message': \"{'player': Alberto}\"}");
            //check that the lose have been done
            assertTrue(model.getPlayers().size()==1);
        }
        else{
            //initialization
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            game.runCommand("2/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Davide,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message':\"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            //lose command
            game.runCommand("1/{'type': \"LOSE\", 'message': \"{'player': Davide}\"}");
            //check that the lose have been done
            assertTrue(model.getPlayers().size()==1);
        }
    }

    @Test
    public void undoTest(){
        game.run();
        Model model = game.getModel();
        assertTrue(game.getNames().size()==2);
        if(game.getChallenger().equals(game.getNames().get(0))){
            //initialization
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            //the second player perform the move
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Alberto}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //perform undo
            game.runCommand("2/{'type': \"UNDO\", 'message': \"{'player': Alberto, 'all': false}\"}");
            //check the success of the undo
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop()==null);
            c = new Coordinates(1,3, model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Alberto}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the second player perform the build
            game.runCommand("2/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 1, 'column': 3}, 'level': 1, 'player': Alberto}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            //perform undoAll
            game.runCommand("2/{'type': \"UNDO\", 'message': \"{'player': Alberto, 'all': true}\"}");
            //check the success of the undoAll
            c = new Coordinates(0, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop()==null);
            c = new Coordinates(1,3, model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==0);
        }
        else{
            //initialization
            game.runCommand("1/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Davide}\"}");
            game.runCommand("2/{'type': \"GODINIT\", 'message': \"{'godName': Atheist,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Alberto,'player': Davide}\"}");
            game.runCommand("2/{'type': \"CHOSESTARTER\", 'message': \"{'starter': Davide,'player': Alberto}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message':\"{'player': Alberto, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            Coordinates c = new Coordinates(1, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 1, 'column': 3}, 'sex': 'f'}\"}");
            game.runCommand("1/{'type': \"WORKERINIT\", 'message': \"{'player': Davide, 'coordinates': {'row': 2, 'column': 4}, 'sex': 'm'}\"}");
            c = new Coordinates(2, 4,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("1/{'type': \"PASS\", 'message': \"{'player': Davide}\"}");
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 4, 'column': 3}, 'sex': 'm'}\"}");
            c = new Coordinates(4, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='m');
            game.runCommand("2/{'type': \"WORKERINIT\", 'message': \"{'player': Alberto, 'coordinates': {'row': 3, 'column': 2}, 'sex': 'f'}\"}");
            c = new Coordinates(3, 2,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Alberto"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            game.runCommand("2/{'type': \"PASS\", 'message': \"{'player': Alberto}\"}");
            //the first player perform the move
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Davide}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //perform undo
            game.runCommand("1/{'type': \"UNDO\", 'message': \"{'player': Davide, 'all': false}\"}");
            //check the success of the undo
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop()==null);
            c = new Coordinates(1,3, model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the first player perform the move
            game.runCommand("1/{'type': \"MOVE\", 'message': \"{'source': {'row': 1, 'column': 3}, 'destination': {'row': 0, 'column': 3}, 'player': Davide}\"}");
            c = new Coordinates(0, 3,model.getBoard());
            //check the presence, the owner and the sex of the worker that perform the move in the new position
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            //the first player perform the build
            game.runCommand("1/{'type': \"BUILD\", 'message': \"{'source': {'row': 0, 'column': 3}, 'destination': {'row': 1, 'column': 3}, 'level': 1, 'player': Davide}\"}");
            c = new Coordinates(1, 3,model.getBoard());
            //check if the level of the cell that has been built is equal to the level of the build
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==1);
            //perform undoAll
            game.runCommand("2/{'type': \"UNDO\", 'message': \"{'player': Davide, 'all': true}\"}");
            //check the success of the undoAll
            c = new Coordinates(0, 3,model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop()==null);
            c = new Coordinates(1,3, model.getBoard());
            assertTrue(model.getBoard().getTopBlock(c).hasWorkerOnTop());
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getPlayer().getNickname().equals("Davide"));
            assertTrue(model.getBoard().getTopBlock(c).getWorkerOnTop().getSex()=='f');
            assertTrue(model.getBoard().getTopBlock(c).getLevel()==0);


        }

    }

    /**
     * Test for the gson functioning
     */
    @Test
    public void test(){
        Gson gson = new Gson();
        boolean b = gson.fromJson("true", boolean.class);
        if(b)
            System.out.println(gson.toJson(b));
    }
}