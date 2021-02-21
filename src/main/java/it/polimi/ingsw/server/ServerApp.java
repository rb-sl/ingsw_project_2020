package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.Client.SocketInfo;
import it.polimi.ingsw.misc.ConfigExporter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Class used to launch the server
 */
public class ServerApp {
    /**
     * Path to the file containing the port number
     */
    private static final String config = "./config/clientConfigs.json";

    /**
     * Main for the application
     * @param args Standard arguments
     */
    public static void main(String[] args)
    {
        ConfigExporter.exportNonExistingConf();

        try (Reader reader = new FileReader(config)) {
            Gson gson = new Gson();
            SocketInfo socketInfo = gson.fromJson(reader, SocketInfo.class);

            Server server;
            try {
                server = new Server(socketInfo.getPortNumber());
                System.out.println("Starting server on " + socketInfo.getHostName() + ":" + socketInfo.getPortNumber());
                server.run();
            } catch (IOException e) {
                System.out.println("Impossible to initialize the server: " + e.getMessage() + "!");
            }
        } catch (IOException e) {
            System.out.println("Error reading the configuration file: " + config);
            System.out.println(e.getMessage());
        }
    }
}
