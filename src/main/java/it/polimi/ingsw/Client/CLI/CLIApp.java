package it.polimi.ingsw.Client.CLI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Client.*;
import it.polimi.ingsw.model.BoardAttribute;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * CLI main app
 */
public class CLIApp {
    /**
     * Main for the application
     * @param args The main's parameters
     */
    public static void main(String[] args) {
        SocketInfo socketInfo;
        CLIView CLIView;
        BoardAttribute boardAttribute = null;
        List<HelpSection> helpMenu = null;
        Map<String, Info> infoCodes = null;
        Gson gson = new Gson().newBuilder().enableComplexMapKeySerialization().create();
        Reader reader;
        try {
            reader = new FileReader("./config/rules.json");
            Type type = new TypeToken<List<HelpSection>>() {}.getType();
            helpMenu = gson.fromJson(reader, type);
            reader = new FileReader("./config/board.json");
            boardAttribute = gson.fromJson(reader, BoardAttribute.class);
            reader = new FileReader("./config/infoCodes.json");
            type = new TypeToken<Map<String, Info>>() {}.getType();
            infoCodes = gson.fromJson(reader, type);
        }
        catch(IOException exception) {
            CLIView = new CLIView();
            CLIView.showInitError("Error while reading configuration files");
        }
        socketInfo = Main.checkAddress(args);
        if (socketInfo == null) {
            //try read from json configuration file
            try
            {
                reader = new FileReader("./config/clientConfigs.json");
                socketInfo = gson.fromJson(reader, SocketInfo.class);
            } catch (IOException exception) {
                CLIView = new CLIView();
                CLIView.showInitError("Error while reading socket configuration");
            }
        }
        try {
            Socket socket = new Socket(socketInfo.getHostName(), socketInfo.getPortNumber());
            CLIView = new CLIView(socket, boardAttribute, helpMenu, infoCodes);
            CLIView.initGame();
        } catch (IOException exception) {
            CLIView = new CLIView();
            CLIView.showInitError("Error while trying to communicate with the server");
        }
    }
}
