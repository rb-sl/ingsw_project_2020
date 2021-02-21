package it.polimi.ingsw.Client.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.Client.Commands.Command;
import it.polimi.ingsw.Client.Commands.CommandAllocator;
import it.polimi.ingsw.Client.Commands.CommandType;
import it.polimi.ingsw.Client.GUI.WelcomeController;
import it.polimi.ingsw.exception.InterruptedReadException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.map.SimpleCoordinates;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cli version of the client
 */
public class CLI extends ViewInterface {
    /**
     * Map associating player's nicknames to their color
     */
    private final Map<String, Color> playerColor;
    /**
     * List of available colors
     */
    private final List<Color> colors;
    /**
     * Scanner used to read from user input
     */
    private final Scanner scanner;
    /**
     * Base number of the menu
     */
    private final Integer MINVALUE = 1;
    /**
     * Queue for the consumer/producer pattern used for the user input
     */
    private final Queue<String> userQueue;
    /**
     * Flag stating if the view owner is the current player
     */
    private boolean isUserTurn = false;

    /**
     * Constructor for the class
     */
    public CLI() {
        colors = Arrays.asList(Color.values());
        scanner = new Scanner(System.in);
        playerColor = new HashMap<>();
        userQueue = new LinkedList<>();
        Thread reader = inputReader();
        reader.start();
    }

    @Override
    public void showWelcome() {
        System.out.println("Welcome to Santorini (CLI version)");
        System.out.println();
        System.out.println("Checking match condition");
    }

    /**
     * Initializes the players' colors
     * @param opponents The set of opponents
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private void initPlayerColor(Set<String> opponents) throws InterruptedReadException {
        Color ownerColor = chooseColor();
        Random rand = new Random();
        playerColor.put(getOwnerNick(), ownerColor);
        for (String nickname : opponents) {
            int nRand = rand.nextInt(colors.size());
            while (playerColor.containsValue(colors.get(nRand)))
                nRand = rand.nextInt(colors.size());
            playerColor.put(nickname, colors.get(nRand));
        }
    }

    /**
     * Lets the player choose their own color
     * @return The chosen color
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private Color chooseColor() throws InterruptedReadException {
        int k = 0;
        System.out.println("These are the available colors to identify you through the match");
        for (Color color : colors) {
            System.out.println((k + 1) + ") " + color.getColor() + "\u2589" + Color.RESET);
            k++;
        }
        int index = chooseLoop("Type in the number of your favourite color: ", colors.size(), MINVALUE);
        System.out.println();
        return colors.get(index);
    }

    @Override
    public void showBoard() {
        Iterator<Map.Entry<String, Color>> entries = playerColor.entrySet().iterator();
        clearView();
        System.out.println();
        for (int i = 0; i < getnRow(); i++) {
            System.out.print("\t\t");
            System.out.print(i + " |");
            System.out.print("  ");
            for (int j = 0; j < getnCol(); j++) {
                if (getMap()[i][j].getWorkerOwner() != null)
                    System.out.print(playerColor.get(getMap()[i][j].getWorkerOwner()).getColor() + getMap()[i][j].getLevelList().get(0) + Color.RESET);
                else
                    System.out.print(getMap()[i][j].getLevelList().get(0));
                System.out.print("  ");
            }
            System.out.print("\t\t");
            if (entries.hasNext()) {
                Map.Entry<String, Color> entry = entries.next();
                System.out.print(playerColor.get(entry.getKey()).getColor() + "\u2589" + entry.getKey() + Color.RESET + " (God: " + getPlayers().get(entry.getKey()) + ")");
            }
            System.out.println();
        }
        System.out.print("\t\t");
        System.out.print("    ");
        for (int j = 0; j < getnCol() * 3; j++) {
            System.out.print("\u2500");
        }
        System.out.println();
        System.out.print("\t\t");
        System.out.print("   ");
        for (int j = 0; j < getnCol(); j++) {
            System.out.print("  ");
            System.out.print(j);
        }
        System.out.println();
        System.out.println();
    }

    @Override
    public InitMatchMessage startNewMatch() {
        InitMatchMessage messageInit = null;
        String ownerNick;
        String answer;
        Integer nPlayers;
        Boolean god = null;
        setUserTurn(true);
        try {
            do {
                System.out.print("Do you wish to start a new game? [Y/N]: ");
                answer = readUserInput();
                if (isLetter(answer)) {
                    answer = answer.toUpperCase();
                    switch (answer) {
                        case "Y": {
                            System.out.print("Insert your nickname: ");
                            ownerNick = readUserInput();
                            do {
                                System.out.print("Insert the number of players (min 2, max 3): ");
                                String input = readUserInput();
                                if (isNumber(input))
                                    nPlayers = Integer.parseInt(input);
                                else
                                    nPlayers = 0;
                                if (nPlayers < 2 || nPlayers > 3)
                                    System.out.println("Wrong number, the number of players must be 2 or 3");
                            } while (nPlayers < 2 || nPlayers > 3);
                            do {
                                System.out.print("Do you wish to play using god cards? [Y/N]: ");
                                answer = readUserInput();
                                if (isLetter(answer)) {
                                    answer = answer.toUpperCase();
                                    switch (answer) {
                                        case "Y":
                                            god = true;
                                            break;
                                        case "N":
                                            god = false;
                                            break;
                                        default:
                                            god = null;
                                            System.out.println("Please, answer using Y or N only");
                                    }
                                } else
                                    System.out.println("Please, answer using Y or N only");
                            } while (god == null);
                            messageInit = new InitMatchMessage(ownerNick, nPlayers, god);
                            break;
                        }
                        case "N": {
                            messageInit = null;
                            setQuitting(true);
                            System.out.println("Press enter key to continue");
                            readUserInput();
                            break;
                        }
                        default: {
                            System.out.println("Please, answer using Y or N only");
                            messageInit = null;
                        }
                    }
                } else
                    System.out.println("Please, answer using Y or N only");
            } while (!(answer.equals("Y") || answer.equals("N")));
        } catch (InterruptedReadException exception) {
            //Thrown only when there are 3 players, 1 disconnected and the 2 remaining have already played a match
            return null;
        }
        setUserTurn(false);
        System.out.println();
        return messageInit;
    }

    @Override
    public JoinMessage joinMatch() {
        String answer;
        String ownerNick;
        JoinMessage joinMessage = null;
        System.out.println("A player already started a new match and is waiting for other players to join");
        setUserTurn(true);
        try {
            do {
                System.out.print("Do you wish to join? [Y/N]: ");
                answer = readUserInput();
                if (isLetter(answer)) {
                    answer = answer.toUpperCase();
                    switch (answer) {
                        case "Y": {
                            System.out.print("Insert your nickname: ");
                            ownerNick = readUserInput();
                            joinMessage = new JoinMessage(ownerNick);
                            break;
                        }
                        case "N": {
                            joinMessage = null;
                            setQuitting(true);
                            System.out.println("Press enter key to continue");
                            readUserInput();
                            break;
                        }
                        default: {
                            System.out.println("Please, answer using Y or N only");
                            joinMessage = null;
                        }
                    }
                } else
                    System.out.println("Please, answer using Y or N only");
            } while (!(answer.equals("Y") || answer.equals("N")));
        } catch (InterruptedReadException exception) {
            //Thrown only when there are 3 players, 1 disconnected and the 2 remaining have already played a match
            return null;
        }
        setUserTurn(false);
        System.out.println();
        return joinMessage;
    }

    @Override
    public void timeout() {
        showInfo("The timer is out. Press enter key to continue");
    }

    @Override
    public void showGod(GodListMessage godList, Gson gson) {
        clearView();
        List<ChosenGodMessage> list = godList.getGodList();
        if (getGods().size() == 0)
            setGods(list);
        System.out.println();
        System.out.println("The following are the gods available to this match: ");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ":" + " " + list.get(i).getName());
            System.out.println("Description: " + list.get(i).getDescription());
            System.out.println();
        }
        if (getOwnerNick().equals(godList.getPlayer())) {
            try {
                ChosenGodMessage godChoose = chooseGod(godList.getGodList());
                GodInitMessage godInitMessage = new GodInitMessage(godChoose.getName(), godList.getPlayer());
                String clientMessage = gson.toJson(godInitMessage, GodInitMessage.class);
                MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.GODINIT, clientMessage);
                Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
            } catch (InterruptedReadException exception) {
                // Does nothing in case of exception
            }
        }
        else {
            waitForChoose(godList.getPlayer());
        }
    }

    @Override
    public void updateMenu(UpdateMessage updateMessage) {
        Boolean hasLost = true;
        CommandType commandType;
        clearUserCommands();
        if (!updateMessage.getReachableCells().values().stream().map(x->x.isEmpty()).reduce(true, (a,b) -> a&&b)) {
            addUserCommand(CommandType.MOVE);
            hasLost = false;
        }
        if (!updateMessage.getBuildableCells().values().stream().map(x->x.isEmpty()).reduce(true, (a,b)->a&&b)) {
            addUserCommand(CommandType.BUILD);
            hasLost = false;
        }
        if (updateMessage.canPass()) {
            addUserCommand(CommandType.PASS);
            hasLost = false;
        }
        if (updateMessage.canUndo()) {
            addUserCommand(CommandType.UNDO);
        }
        addUserCommand(CommandType.HELP);
        if (!hasLost) {
            do {
                commandType = chooseAction();
                if (commandType == null)
                    return;
                Command command = CommandAllocator.allocateCommand(this, new Gson(), updateMessage, commandType);
                command.execute();
                if (commandType.equals(CommandType.HELP))
                    showBoard();
            } while (commandType.equals(CommandType.HELP));
        }
        else
        {
            //A player can't lose if its worker has moved.
            if (!hasMoved()) {
                showLose();
                SimpleMessage loseMessage = new SimpleMessage(getOwnerNick());
                Gson gson = new Gson();
                String toSend = gson.toJson(loseMessage, SimpleMessage.class);
                MessageEnvelope envelope = new MessageEnvelope(MessageType.LOSE, toSend);
                Command.sendMessage(gson.toJson(envelope, MessageEnvelope.class));
                setQuitting(true);
            }
        }
    }

    @Override
    public void showLose() {
        System.out.println("Sorry, you are not able to move/build with any of your workers. You lose");
    }

    @Override
    public void waitForChoose(String activePlayer) {
        System.out.println(activePlayer + " is choosing their favourite god");
    }

    @Override
    public void reset() {
        setOwnerNick(null);
        resetData();
        setCanPass(false);
        setInit(false);
        setQuitting(false);
        playerColor.clear();
        clearUserCommands();
    }

    /**
     * Lets the player choose their favourite god
     * @param list The list of gods still available
     * @return The message to be sent to the server
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private ChosenGodMessage chooseGod(List<ChosenGodMessage> list) throws InterruptedReadException {
        Integer index;
        index = chooseLoop("Choose your favourite god through its number: ", list.size(), MINVALUE);
        return list.get(index);
    }

    /**
     * Lets the challenger choose the starter player
     * @param players The map associating every player to their gods
     * @return The nickname of the starter player
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private String chooseStarter(Map<String, String> players) throws InterruptedReadException{
        Integer chosenOne;
        List<String> playersList;
        chosenOne = chooseLoop("Choose the starter player for this match: ", players.size(), MINVALUE);
        playersList = new ArrayList<>(players.keySet());
        return playersList.get(chosenOne);
    }

    /**
     * Shows the players to the challenger
     * @param players The Map of players associated to their gods
     */
    private void showPlayers(Map<String, String> players){
        int k = 0;
        System.out.println("You have been chosen to pick the starter player for this match");
        System.out.println("The following are the current players: ");
        for (String key : players.keySet()) {
            System.out.println((k + 1) + ": " + playerColor.get(key).getColor() + key + Color.RESET + " (God: " + players.get(key) + ")");
            k++;
        }
        System.out.println();
    }

    @Override
    public void showOpponents(PlayersMessage players, String challenger) {
        int k = 0;
        Map<String, String> opponents = players.getPlayers().entrySet().stream().filter(entry -> !entry.getKey().equals(getOwnerNick()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        try {
            initPlayerColor(opponents.keySet());
            if (opponents.size() > 1)
                System.out.println("Your opponents are: ");
            else
                System.out.println("Your opponent is: ");
            for (String key : opponents.keySet()) {
                System.out.println((k + 1) + ": " + playerColor.get(key).getColor() + key + Color.RESET + " (God: " + opponents.get(key) + ")");
                k++;
            }
            System.out.println();
            if (challenger.equals(getOwnerNick())) {
                Gson gson = new Gson();
                showPlayers(players.getPlayers());
                String starter = chooseStarter(players.getPlayers());
                StarterMessage starterMessage = new StarterMessage(starter, getOwnerNick());
                String sendMessage = gson.toJson(starterMessage, StarterMessage.class);
                MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.CHOSESTARTER, sendMessage);
                Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));

            }
            else {
                challengerMessage(challenger);
            }
        } catch (InterruptedReadException exception) {
            // Does nothing in case of exception
        }
        setPlayers(players.getPlayers());
    }

    @Override
    public void showOwnerGod() {
        String name = getPlayers().get(getOwnerNick());
        System.out.println("Your God is " + getPlayers().get(getOwnerNick()));
        System.out.println(getGods().get(name));
        System.out.println();
    }

    @Override
    public void showHelp() {
        for (HelpSection section: getHelpSections()) {
            recursivePrint(section);
        }
        System.out.println();
        waitForUser();
    }

    /**
     * Prints the rules
     * @param section The section to print
     */
    private void recursivePrint(HelpSection section) {
        System.out.println(Color.ANSI_CYAN.getColor() + section.getSection().toUpperCase() + Color.RESET);
        if (section.getText() != null)
            System.out.println(section.getText());
        if (section.getSubSection() != null) {
            for (HelpSection subsection : section.getSubSection())
                recursivePrint(subsection);
        }
    }

    public void placeWorker(char sex) {
        WorkerInitMessage workerInitMessage;
        Integer row = 0, col = 0;
        try {
            System.out.println("Please choose where you want to place your " + sex + " worker");
            row = chooseLoop("Row: ", getnRow() - 1, MINVALUE - 1);
            row += 1;
            col = chooseLoop("Column: ", getnCol() - 1, MINVALUE - 1);
            col += 1;
            workerInitMessage = new WorkerInitMessage(new SimpleCoordinates(row, col), getOwnerNick(), sex);
            Gson gson = new Gson();
            String toSend = gson.toJson(workerInitMessage, WorkerInitMessage.class);
            MessageEnvelope sendEnvelope = new MessageEnvelope(MessageType.WORKERINIT, toSend);
            Command.sendMessage(gson.toJson(sendEnvelope, MessageEnvelope.class));
        } catch (InterruptedReadException exception) {
            // Does nothing in case of exception
        }
    }

    public CommandType chooseAction() {
        int k = 0;
        int index;
        System.out.println("Available commands:");
        for (CommandType command : getUserCommands()) {
            System.out.println((k + 1) + ") " + command.getCommand());
            k++;
        }
        try {
            index = chooseLoop("Choose your next action: ", getUserCommands().size(), MINVALUE);
        }
        catch (InterruptedReadException exception) {
            return null;
        }
        System.out.println();
        return getUserCommands().get(index);
    }

    @Override
    public MoveMessage move(Map<SimpleCoordinates, List<SimpleCoordinates>> reachableCells) {
        SimpleCoordinates workerPosition;
        List<SimpleCoordinates> workerReachables;
        Integer workerIndex, positionIndex;
        MoveMessage moveMessage;
        try {
            List<SimpleCoordinates> movables = reachableCells.keySet().stream().filter(k -> !reachableCells.get(k).isEmpty()).collect(Collectors.toList());
            showWorkers(movables);
            workerIndex = chooseLoop("Type in the number of the worker to be moved: ", movables.size(), MINVALUE);
            System.out.println("Reachable cells for this worker: ");
            workerPosition = movables.get(workerIndex);
            workerReachables = reachableCells.get(workerPosition);
            positionIndex = choosePosition(workerReachables);
            System.out.println();
            setHasMoved(true);
            moveMessage = new MoveMessage(workerPosition, workerReachables.get(positionIndex), getOwnerNick());
        }
        catch(InterruptedReadException exception) {
            return null;
        }
        return moveMessage;
    }

    @Override
    public BuildMessage build(Map<SimpleCoordinates, Map<SimpleCoordinates, List<Integer>>> buildableCells) {
        SimpleCoordinates workerPosition;
        Integer level;
        List<Integer> buildableLevels;
        Map<SimpleCoordinates, List<Integer>> workerBuildables;
        List<SimpleCoordinates> buildableList;
        Integer workerIndex;
        SimpleCoordinates buildPosition;
        BuildMessage buildMessage;
        try {
            System.out.println();
            List<SimpleCoordinates> builders = buildableCells.keySet().stream().filter(k -> !buildableCells.get(k).isEmpty()).collect(Collectors.toList());
            showWorkers(builders);
            workerIndex = chooseLoop("Type in the number of the worker you want to build with: ", builders.size(), MINVALUE);
            System.out.println("Buildable cells for this worker: ");
            workerPosition = builders.get(workerIndex);
            workerBuildables = buildableCells.get(workerPosition);
            buildableList = new ArrayList<>(workerBuildables.keySet());
            buildPosition = buildableList.get(chooseBuildPos(workerBuildables));
            buildableLevels = workerBuildables.get(buildPosition);
            if (buildableLevels.size() > 1) {
                level = chooseLevel(buildableLevels);
            } else
                level = 0;
            System.out.println();
            buildMessage = new BuildMessage(workerPosition, buildPosition, buildableLevels.get(level), getOwnerNick());
        }
        catch (InterruptedReadException exception) {
            return null;
        }
        return buildMessage;
    }

    /**
     * Shows the available workers
     * @param workersPos The list of workers' positions
     */
    private void showWorkers(List<SimpleCoordinates> workersPos) {
        int k = 0;
        System.out.println("These are your available workers (row, column): ");
        for (SimpleCoordinates coor : workersPos) {
            //Character is the key, cannot return more than one character
            Character sex = getWorkers().keySet().stream().filter(key -> getWorkers().get(key).equals(coor)).findFirst().get();
            System.out.println((k + 1) + ") " + "Worker " + sex + " Position: " + "(" + coor.getRow() + ", " + coor.getColumn() + ") ");
            k++;
        }
    }

    /**
     * Lets the player move a worker
     * @param reachableCells The list of reachable cells
     * @return The chosen destination
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private Integer choosePosition(List<SimpleCoordinates> reachableCells) throws InterruptedReadException {
        Integer chosenPosition;
        System.out.println("These are the available cells where you can move your worker (row, column): ");
        for (int i = 0; i < reachableCells.size(); i++) {
            System.out.println((i + 1) + ") " + "Position: " + "(" + reachableCells.get(i).getRow() + ", " + reachableCells.get(i).getColumn() + ") ");
        }
        chosenPosition = chooseLoop("Type the number of the coordinate where you want to position the worker: ", reachableCells.size(), MINVALUE);
        return chosenPosition;
    }

    /**
     * Lets the player build a block
     * @param buildableCells The list of buildable cells associated to the heights
     * @return The chosen position
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private Integer chooseBuildPos(Map<SimpleCoordinates, List<Integer>> buildableCells) throws InterruptedReadException {
        Integer chosenPosition;
        int k = 0;
        System.out.println("These are the available cells where you can build onto (row, column): ");
        for (SimpleCoordinates coor : buildableCells.keySet()) {
            System.out.print((k + 1) + ") " + "Position: " + "(" + coor.getRow() + ", " + coor.getColumn() + ") ");
            System.out.print("Buildable levels: ");
            for (int j = 0; j < buildableCells.get(coor).size(); j++) {
                System.out.print(buildableCells.get(coor).get(j));
                if (j < buildableCells.get(coor).size() - 1)
                    System.out.print(", ");
            }
            k++;
            System.out.println();
        }
        chosenPosition = chooseLoop("Type the number of the coordinate where you want to build: ", buildableCells.size(), MINVALUE);
        return chosenPosition;
    }

    /**
     * Lets the player choose the level of their build
     * @param buildableLevels The list of buildable levels
     * @return The chosen level
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private Integer chooseLevel(List<Integer> buildableLevels) throws InterruptedReadException {
        Integer chosenLevel;
        for (int i = 0; i < buildableLevels.size(); i++) {
            System.out.println((i + 1) + ") Level " + buildableLevels.get(i));
        }
        chosenLevel = chooseLoop("Type the number of the corresponding level: ", buildableLevels.size(), MINVALUE);
        return chosenLevel;
    }

    @Override
    public void pass() {
        setCanPass(true);
        System.out.println("From now on you have 5 seconds to undo all your turn. Press the enter key to Undo your turn");
        Timer timer = new Timer();
        timer.schedule(new TimerClass(this), 5000);
        setUserTurn(true);
        try {
            readUserInput();
        } catch (InterruptedReadException exception) {
            setUserTurn(false);
            timer.purge();
            timer.cancel();
        }
        setUserTurn(false);
        setHasMoved(false);
        timer.purge();
        timer.cancel();
        MessageEnvelope envelope;
        if (canPass()) {
            Gson gson = new Gson();
            UndoMessage undoMessage = new UndoMessage(getOwnerNick(), true);
            String message = gson.toJson(undoMessage, UndoMessage.class);
            envelope = new MessageEnvelope(MessageType.UNDO, message);
            Command.sendMessage(gson.toJson(envelope, MessageEnvelope.class));
        }
    }

    /**
     * Shows the text associated to an action
     * @param message The message to display
     * @param maxValue The max choosable number
     * @param minValue The min choosable value
     * @return The chosen index
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private Integer chooseLoop(String message, Integer maxValue, Integer minValue) throws InterruptedReadException {
        String input;
        Integer index;
        setUserTurn(true);
        do {
            System.out.print(message);
            input = readUserInput();
            if (isNumber(input))
                index = Integer.parseInt(input);
            else
                index = -1;
            if (index < minValue || index > maxValue) {
                wrongNumber(minValue, maxValue);
            }
        } while (index < minValue || index > maxValue);
        setUserTurn(false);
        index -= 1;
        return index;
    }

    @Override
    public void endGame(String winner) {
        if (winner.equals(getOwnerNick())) {
            System.out.println("Congratulations, you are the winner!");
        } else
            System.out.println("You lose but great match! The winner is: " + winner);
    }

    @Override
    public void disconnected(String player) {
        System.out.println("The game ended due to the player " + player + "'s disconnection");
    }

    /**
     * Waits for the user input
     */
    private void waitForUser() {
        setUserTurn(true);
        try {
            System.out.print("Press the enter key to continue.");
            readUserInput();
        } catch (InterruptedReadException exception) {
            return;
        }
        setUserTurn(false);
        clearView();
    }

    @Override
    public void setWelcomeController(WelcomeController controller) {
        // Does nothing in CLI
    }

    @Override
    public void showInfo(String code) {
        Info info = getInfoCodes().get(code);
        if (info == null) {
            //When the client has to print its own infos. Not inserted in infoCodes.json because there are variable infos with
            //extra dynamic information
            System.out.println(code);
        }
        else
            //info sent by the server
            System.out.println(getInfoCodes().get(code).text);
    }

    @Override
    public void waitMessage() {
        System.out.println("Waiting for other players to join");
    }

    @Override
    public void waitForTurn(String opponent) {
        System.out.println("Wait for your turn while " + opponent + " is playing");
        System.out.println();
    }

    @Override
    public void challengerMessage(String challengerNick) {
        System.out.println("Wait for " + challengerNick + " to choose the starter player");
    }

    @Override
    public void showError(String error) {
        System.out.println(error);
    }

    /**
     * Print the wrong number message
     * @param minValue The minimum acceptable value
     * @param maxValue The maximum acceptable value
     */
    private void wrongNumber(Integer minValue, Integer maxValue) {
        System.out.println("Wrong number, please try again. (min = " + minValue + ", max = " + maxValue + ")");

    }

    /**
     * Clears the console
     */
    private void clearView() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Consumer for the consumer/producer pattern to read the user input
     * @return The read message
     * @throws InterruptedReadException If the reader thread is interrupted
     */
    private String readUserInput() throws InterruptedReadException {
        synchronized(userQueue) {
            while (userQueue.isEmpty()) {
                try {
                    userQueue.wait();
                } catch(InterruptedException exception){
                    throw new InterruptedReadException();
                }
            }
            return userQueue.poll();
        }
    }

    /**
     * Producer for the consumer/producer pattern
     * @return The reader thread
     */
    private Thread inputReader() {
        return new Thread(() -> {
            while (true) {
                String input = scanner.nextLine();
                if (isUserTurn) {
                    synchronized (userQueue) {
                        userQueue.add(input);
                        userQueue.notify();
                    }
                }
            }
        });
    }

    /**
     * Sets the user turn flag
     * @param isUserTurn The value to set
     */
    private void setUserTurn(boolean isUserTurn) {
        this.isUserTurn = isUserTurn;
    }
}
