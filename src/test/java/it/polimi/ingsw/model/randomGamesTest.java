package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.exception.HasWonException;
import it.polimi.ingsw.misc.ConfigExporter;
import it.polimi.ingsw.model.god.God;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Coordinates;
import it.polimi.ingsw.model.map.MapComponent;
import it.polimi.ingsw.model.map.SimpleCoordinates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Test used to execute random games. Checks that no infinite loops happen and let follow random games to control that
 * there isn't any unwanted effects related to god powers.
 * Generates statistics related to wins and game states (the file path is showed at the end of the tests)
 * WARNING: resource-intensive, will take some time
 */
public class randomGamesTest {
    // Variables for color management
    private static final String[] godList = {"Apollo", "Artemis", "Athena", "Atlas", "Demeter", "Hephaestus", "Minotaur", "Pan", "Prometheus", "Hera", "Hestia", "Hypnus", "Limus", "Triton"};
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String[] color = {"\u001B[31m","\u001B[33m","\u001B[34m"};
    private Map<String, String> playerToColor;

    // Game configuration
    private static final int nRow = 5;
    private static final int nCol = 5;
    private static final int nMatches = 10;
    public static final int nWorkers = 2;
    private static final Integer undoProbability = 10;

    // Game variables
    private Model model;
    private List<Player> players;
    private Random random;
    private static Integer gamesPlayed;

    // Variables used for statistics
    private Map<String,Integer> nWins;
    private Map<String,Map<String,Integer>> winnerAgainst;
    private Map<SimpleCoordinates,Integer> finalWorkerPos;
    private static final DecimalFormat df = new DecimalFormat();

    /**
     * Launches nMatches 2-players games without gods
     */
    @Test
    public void twoPlayersNoGods() {
        initGame();

        for(int k = 0; k < nMatches; k++) {
            random = new Random(System.currentTimeMillis());
            modelGame(2, null);
        }
        outputReport(2, false);
    }

    /**
     * Launches nMatches 3-players games without gods
     */
    @Test
    public void threePlayersNoGods() {
        initGame();

        for(int k = 0; k < nMatches; k++) {
            random = new Random(System.currentTimeMillis());
            modelGame(3, null);
        }
        outputReport(3, false);
    }

    /**
     * Launches 2-players games with gods; every god couple plays nMatches times
     */
    @Test
    public void twoPlayersGods() {
        // 2 players interactions
        initGame();

        for(int k = 0; k < nMatches; k++) {
            for (int i = 0; i < godList.length; i++) {
                for (int j = i + 1; j < godList.length; j++) {
                    random = new Random(System.currentTimeMillis());

                    List<String> l = new ArrayList<>();
                    l.add(godList[i]);
                    l.add(godList[j]);
                    System.out.println();
                    System.out.println(godList[i] + " vs " + godList[j]);

                    modelGame(2, l);
                }
            }
        }
        outputReport(2, true);
    }

    /**
     * Launches 3-players games with gods; every god triplet plays nMatches times
     */
    @Test
    public void threePlayersGods() {
        initGame();

        for(int k = 0; k < nMatches; k++) {
            for (int i = 0; i < godList.length; i++) {
                for (int j = i + 1; j < godList.length; j++) {
                    for (int v = j + 1; v < godList.length; v++) {
                        random = new Random(System.currentTimeMillis());

                        List<String> l = new ArrayList<>();
                        l.add(godList[i]);
                        l.add(godList[j]);
                        l.add(godList[v]);
                        System.out.println();
                        System.out.println(godList[i] + " vs " + godList[j] + " vs " + godList[v]);

                        modelGame(3, l);
                    }
                }
            }
        }
        outputReport(3, true);
    }

    /**
     * Game simulator using only model-related classes
     * @param nPlayers The number of playing players
     * @param godList The (optional) list of gods playing
     */
    public void modelGame(Integer nPlayers, List<String> godList) {
        // Initializing the model
        players = new ArrayList<>();
        model = new Model(nPlayers, godList != null);

        try {
            model.initBoard();
        } catch (BadConfigurationException e) {
            e.printStackTrace();
        }

        // Creating the players
        for (int i = 0; i < nPlayers; i++) {
            String nickname = "Player" + i;
            Player p = new Player(nickname, model.getBoard());
            playerToColor.put(nickname, color[i]);
            nWins.putIfAbsent(nickname, 0);

            if(godList != null) {
                System.out.println(nickname + " got " + godList.get(i));
                p.setGod(new God(godList.get(i), p));
            }
            else {
                p.setGod(new God("NoGod", p));
            }
            players.add(p);
        }

        System.out.println("Starting a new " + nPlayers + "-players game " + (godList != null ? "with" : "without") + " gods");

        // Choosing the starter and initializing the game
        int turn = random.nextInt(players.size());
        model.setTurn(turn);

        model.addPlayers(players);
        if(godList != null) {
            model.initGods();
        }

        // Worker placement phase
        for (int i = 0; i < nPlayers; i++) {
            Player p = players.get(turn);

            // Place workers until the chosen coordinates are correct
            int workersPlaced = 0;
            while (workersPlaced < nWorkers) {
                Coordinates workerCoordinates = new Coordinates(random.nextInt(nRow), random.nextInt(nCol), model.getBoard());

                if (model.initWorker(p, (char) (i + 48), workerCoordinates)) {
                    workersPlaced++;
                    System.out.println(p.getNickname() + " - Placed worker " + workersPlaced + " on (" + workerCoordinates.getRow() + ";" + workerCoordinates.getColumn() + ")");
                }
            }

            // Change turn
            model.endTurn();
            turn = (turn + 1) % nPlayers;
        }

        System.out.println("Initial board state:");
        printBoard(model.getBoard());

        // Normal game
        boolean gameOn = true;
        Map<Coordinates, List<Integer>> buildableCells;
        List<Coordinates> reachableCells;
        while (gameOn) {
            // Starts the turn
            Player p = players.get(turn);
            List<Worker> workerList = p.getWorkers();

            for (Worker w : workerList) {
                w.setTimesMoved(0);
                w.setTimesBuilt(0);
            }

            int currentWorkerIndex = random.nextInt(nWorkers);

            Worker currentWorker = workerList.get(currentWorkerIndex);
            reachableCells = currentWorker.reachableCells();
            buildableCells = currentWorker.buildableCells();

            // If the selected worker cannot move chooses the other
            if (reachableCells.isEmpty()) {
                currentWorkerIndex = (currentWorkerIndex + 1) % nWorkers;
                currentWorker = workerList.get(currentWorkerIndex);
                reachableCells = currentWorker.reachableCells();
                buildableCells = currentWorker.buildableCells();

                //if both workers cannot move nor build at the start of the turn the player loses
                if (reachableCells.isEmpty()) {
                    // Logs the final position
                    for (Worker worker : p.getWorkers()) {
                        finalWorkerPos.compute(worker.getCurrentPosition().getSimpleCoor(), (x, y) -> y = y + 1);
                    }
                    // Removes the player
                    players.remove(p);
                    p.getWorkers().forEach(worker -> worker.getCurrentPosition().getTopBlock().removeWorkerOnTop());
                    nPlayers--;
                    System.out.println(">>> " + p.getNickname() + " lost!");
                    if (players.size() == 1) {
                        System.out.println(">>> " + players.get(0).getNickname() + " won (last standing)!");

                        // Logs the winner's workers' final positions
                        for (Worker worker : players.get(0).getWorkers()) {
                            finalWorkerPos.compute(worker.getCurrentPosition().getSimpleCoor(), (x, y) -> y = y + 1);
                        }

                        // Adds a win to the player's god, if present
                        nWins.replace(players.get(0).getNickname(), nWins.get(players.get(0).getNickname()) + 1);
                        if(godList != null) {
                            players.get(0).getOpponents().stream().map(a -> a.getGod().getGodName()).forEach(loser -> winnerAgainst.get(players.get(0).getGod().getGodName()).computeIfPresent(loser, (x, y) -> y = y + 1));
                        }
                        // Ends the game
                        gameOn = false;
                    }
                    turn = (turn + 1) % nPlayers;
                    continue;
                }
            }

            // The current player does anything it can; random values are used to "decide"
            while (!reachableCells.isEmpty() || !buildableCells.isEmpty()) {
                // Move conditions
                if (!reachableCells.isEmpty() && random.nextBoolean() || buildableCells.isEmpty()) {
                    try {
                        currentWorker.moveTo(reachableCells.get(random.nextInt(reachableCells.size())));
                    } catch (HasWonException e) {
                        System.out.println(">>> " + p.getNickname() + " won!");

                        // Adds a win to the player's god, if present
                        nWins.replace(p.getNickname(), nWins.get(p.getNickname()) + 1);

                        if(godList != null) {
                            p.getOpponents().stream().map(a -> a.getGod().getGodName()).forEach(loser -> winnerAgainst.get(p.getGod().getGodName()).computeIfPresent(loser, (x, y) -> y = y + 1));
                        }
                        // Logs every player's workers' positions
                        for (Player player : players) {
                            for (Worker worker : player.getWorkers()) {
                                finalWorkerPos.compute(worker.getCurrentPosition().getSimpleCoor(), (x, y) -> y = y + 1);
                            }
                        }

                        // Ends the game
                        gameOn = false;
                        break;
                    } finally {
                        System.out.println(p.getNickname() + " moved worker " + currentWorkerIndex + " to (" + currentWorker.getCurrentPosition().getRow() + ";" + currentWorker.getCurrentPosition().getColumn() + ")");
                    }
                } else {
                    // Chooses the build destination
                    Coordinates buildCoordinates = new ArrayList<>(buildableCells.keySet()).get(random.nextInt(buildableCells.keySet().size()));
                    // Chooses the build level
                    int levelPos = random.nextInt(buildableCells.get(buildCoordinates).size());
                    currentWorker.buildTo(buildCoordinates, buildableCells.get(buildCoordinates).get(levelPos));

                    System.out.println(p.getNickname() + " built with worker " + currentWorkerIndex + " to (" + buildCoordinates.getRow() + ";" + buildCoordinates.getColumn() + ")");
                }

                printBoard(model.getBoard());

                // Undoes the action with frequency 1/undoProbability
                if (random.nextInt(undoProbability) == undoProbability - 1) {
                    p.getGod().getLogTurn().undo();
                    System.out.println(p.getNickname() + " undid his last action.");
                    printBoard(model.getBoard());
                }

                // Checks for changed data
                reachableCells = currentWorker.reachableCells();
                buildableCells = currentWorker.buildableCells();
            }

            // Ends the turn
            p.getGod().resetTriggers();
            p.getGod().getLogTurn().reset();
            turn = (turn + 1) % nPlayers;

            random = new Random(System.currentTimeMillis());
        }

        // Ends the game
        System.out.println(">>> Game ended! Final board state: ");
        printBoard(model.getBoard());
        gamesPlayed++;
    }

    /**
     * Function to print a board
     * @param board The board to print
     */
    public void printBoard(Board board) {
        for(int i = nRow-1; i >= 0; i--) {
            for(int j = 0; j < nCol; j++) {
                MapComponent block = new Coordinates(j,i,board).getTopBlock();

                if(block.hasWorkerOnTop()) {
                    System.out.print(playerToColor.get(block.getWorkerOnTop().getPlayer().getNickname()) + block.getLevel() + ANSI_RESET + "  ");
                }
                else {
                    System.out.print(block.getLevel() + "  ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Initializes the game variables
     */
    public void initGame() {
        ConfigExporter.exportNonExistingConf();
        playerToColor = new HashMap<>();
        nWins = new HashMap<>();
        winnerAgainst = new HashMap<>();
        finalWorkerPos = new HashMap<>();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        gamesPlayed = 0;

        for(int a = 0; a < godList.length; a++) {
            Map<String,Integer> loser = new HashMap<>();
            for(int b = 0; b < godList.length; b++) {
                if (b != a) {
                    loser.put(godList[b], 0);
                }
            }
            winnerAgainst.put(godList[a],loser);

            for(int i = 0; i < nRow; i++) {
                for(int k = 0; k < nCol; k++) {
                    finalWorkerPos.put(new SimpleCoordinates(i, k), 0);
                }
            }
        }
    }

    /**
     * Prints the reports
     * @param nPlayers The number of players
     * @param withGods Defines whether the stats related to gods have to be printed
     */
    public void outputReport(Integer nPlayers, boolean withGods) {
        try (Writer writer = new FileWriter(System.getProperty("user.dir") + "/testFile/log_" + nPlayers + "p_" + (withGods ? "gods" : "nogods") + ".txt")) {

            writer.append(">>> Games played: ").append(String.valueOf(gamesPlayed)).append("\n");

            // Logging wins per player
            writer.append("\n>>> Wins per player:\n");
            nWins.forEach((p, w) -> {
                try {
                    writer.append(p).append(": ").append(String.valueOf(w)).append(" times (").append(df.format(((float) w / gamesPlayed) * 100)).append("%)\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            df.setMinimumIntegerDigits(2);
            // Logging final positions
            writer.append("\n>>> Final workers' positions distributions:\n");
            int totalWorkers = nPlayers * nWorkers * gamesPlayed;
            for(Integer i = nRow - 1; i >= 0; i--) {
                writer.append("+--------+--------+--------+--------+--------+\n");
                for(int j = 0; j < nCol; j++) {
                    Float workersPerCell =  ((float) finalWorkerPos.get(new SimpleCoordinates(i, j)) / totalWorkers) * 100;
                    writer.append("| ").append(df.format(workersPerCell)).append("% ");
                }
                writer.append("|\n");
            }
            writer.append("+--------+--------+--------+--------+--------+\n");


            // Logs gods-related statistics
            if(withGods) {
                writer.append("\n>>> God-related win statistics\n+");
                for (int i = 0; i <= godList.length; i++) {
                    writer.append("------------+");
                }
                writer.append("\n| Winners -> | ");
                for (String s: godList) {
                    writer.append(s);

                    for (int a = s.length(); a < 10; a++) {
                        writer.append(" ");
                    }
                    writer.append(" | ");
                }

                writer.append("\n+");
                for (int i = 0; i <= godList.length; i++) {
                    writer.append("------------+");
                }
                writer.append("\n");

                for (int i = 0; i < godList.length; i++) {
                    writer.append("| ").append(godList[i]);
                    for (int a = godList[i].length(); a < 10; a++) {
                        writer.append(" ");
                    }

                    for (int j = 0; j < godList.length; j++) {
                        if (i != j) {
                            Integer n = winnerAgainst.get(godList[j]).get(godList[i]);

                            writer.append(" | ").append(n.toString());

                            for (int a = n.toString().length(); a < 10; a++) {
                                writer.append(" ");
                            }
                        } else {
                            writer.append(" | -");
                            for (int a = 1; a < 10; a++) {
                                writer.append(" ");
                            }
                        }
                    }

                    writer.append(" |\n+");
                    for (int r = 0; r <= godList.length; r++) {
                        writer.append("------------+");
                    }
                    writer.append("\n");
                }

                Map<Integer, Integer> winsPerGod = new HashMap<>();
                writer.append("| TOTAL ->  ");
                for (int i = 0; i < godList.length; i++) {
                    Integer n = winnerAgainst.get(godList[i]).values().stream().reduce(0, Integer::sum);
                    winsPerGod.put(i, n);
                    writer.append(" | ").append(n.toString());
                    for (int a = n.toString().length(); a < 10; a++) {
                        writer.append(" ");
                    }
                }

                writer.append(" |\n+");
                for (int r = 0; r <= godList.length; r++) {
                    writer.append("------------+");
                }

                writer.append("\n| %     ->  ");
                for (int i = 0; i < godList.length; i++) {
                    Float nPercent = ((float) winsPerGod.get(i) / (nMatches * (godList.length - 1) * (nPlayers == 3 ? godList.length - 2 : 1))) * 100;
                    writer.append(" | ").append(df.format(nPercent));
                    for (int a = df.format(nPercent).length(); a < 10; a++) {
                        writer.append(" ");
                    }
                }

                writer.append(" |\n+");
                for (int r = 0; r <= godList.length; r++) {
                    writer.append("------------+");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("🦙🦙🦙🦙🦙🦙🦙🦙🦙🦙");
        System.out.println("🦙 Simulation ended 🦙");
        System.out.println("🦙🦙🦙🦙🦙🦙🦙🦙🦙🦙");

        System.out.println();
        System.out.println("Report location: " + System.getProperty("user.dir") + "/testfile/log_" + nPlayers + "p_" + (withGods ? "gods" : "nogods") + ".txt");
    }
}
