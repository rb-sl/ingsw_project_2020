package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.misc.ConfigExporter;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

/**
 * This class manage the creation of a local game
 */
public class LocalGame {
    /**
     * Number of players that will play the game
     */
    private Integer nplayer;
    /**
     * List with the nicknames of the players
     */
    private final List<String> names;
    /**
     * List with the view of each player
     */
    private final List<LocalView> views;
    /**
     * List with the players
     */
    private final List<Player> players;
    /**
     * Scanner for the reading of the first commands
     */
    private Scanner scanner;
    /**
     * This attribute contains the nickname of the challenger
     */
    private String challenger;
    /**
     * This attribute contains the model
     */
    private Model model;
    /**
     * This flag indicates if the game uses gods or not
     */
    private boolean withGods;

    /**
     * Constructor of the class
     */
    public LocalGame() {
        ConfigExporter.exportNonExistingConf();
        this.nplayer = 2;
        this.names = new ArrayList<>();
        this.views = new ArrayList<>();
        this.players = new ArrayList<>();
        File moves = new File("TestFile\\moveSet.json");
        try {
            this.scanner = new Scanner(moves, StandardCharsets.UTF_8.name());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the attribute model
     * @return the attribute model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Getter for the attribute names
     * @return List of players' names
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * This method first add the player to the lobby, then when there are as many players as nplayers
     * instantiates the model
     * @param string this is the command that players send to the lobby to register. If the lobby is empty,
     *               the first player chooses the number of players and if the game will have the gods. The other
     *               players only send their nicknames
     */
    public void lobby(String string){
        String[] input = string.split(" ");
        //add the name to the list of nicknames
        names.add(input[0]);
        //if there is only one player set the number of players and if play with gods or not
        if(names.size() == 1) {
            this.nplayer = Integer.parseInt(input[1]);
            if(input[2].equals("God"))
                this.withGods = true;
            else if(input[2].equals("NoGod"))
                this.withGods = false;
        }//when there are as many players as nplayers istantiates the game
        if(names.size() == nplayer){
            //create the model
            model = new Model(nplayer, withGods);
            //initialize the board
            try {
                model.initBoard();
            } catch (BadConfigurationException e) {
                System.out.println(e.getMessage());
            }
            //create the player entity with the right nickname
            for(int i = 0; i < nplayer; i++)
                players.add(new Player(names.get(i), model.getBoard()));
            //add players to the model
            model.addPlayers(players);
            //create the handlers that will be the controller in the MVC pattern
            GodInitHandler godInitHandler = new GodInitHandler(model);
            ChooseStarterHandler chooseStarterHandler = new ChooseStarterHandler(model);
            WorkerInitHandler workerInitHandler = new WorkerInitHandler(model);
            MoveHandler moveHandler = new MoveHandler(model);
            BuildHandler buildHandler = new BuildHandler(model);
            PassHandler passHandler = new PassHandler(model);
            UndoHandler undoHandler = new UndoHandler(model);
            LoseHandler loseHandler = new LoseHandler(model);
            //create one view for each player and add it to the list of Observers in the model
            for(int i = 0; i < nplayer; i++) {
                views.add(new LocalView(players.get(i), godInitHandler, chooseStarterHandler, workerInitHandler, moveHandler, buildHandler, passHandler, undoHandler, loseHandler));
                model.addObserver(views.get(i));
            }
            //extract the challenger
            model.setChallenger();
            challenger = model.getChallenger();
            //if the is with gods, extract the gods that will be used in this game
            if(withGods)
                model.getGodList();
        }
    }

    /**
     * Getter for attribute challenger
     * @return the attribute challenger
     */
    public String getChallenger() {
        return challenger;
    }

    /**
     * This method verify the player that has sent the command  and then sends it to the right view
     * @param s this is the string with the command and the player that sent it
     */
    public void runCommand(String s) {
        if(s!=null) {
            Gson gson = new Gson();
            String[] input = s.split("/");
            Integer player = Integer.parseInt(input[0]);
            String comando = input[1];
            MessageEnvelope m = gson.fromJson(comando, MessageEnvelope.class);
            views.get(player - 1).handleMessage(m);
        }
    }

    /**
     * This method start the sign in to the lobby
     */
    public void run(){
        while(names.size() < nplayer) {
            String s = scanner.nextLine();
            lobby(s);
        }
    }
}

