package it.polimi.ingsw.misc;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Class used to export the configuration files next to the jar, if not already present
 */
public class ConfigExporter {
    /**
     * Path from the resource root
     */
    private static final String resourceConfPath = "/config/";
    /**
     * Path to which the configuration files need to be copied
     */
    private static final String extConfPath = "./config/";

    /**
     * Copies only the non-existing configuration files from the resource root to the external path
     */
    public static void exportNonExistingConf() {
        boolean madeDir = false;
        try {
            // Creates the directory if necessary
            File confDir = new File(extConfPath);
            if(!confDir.exists()) {
                madeDir = confDir.mkdir();
            }

            // Gets the uri and, if necessary, creates a file system to access the resources inside the jar
            Path path;
            URI uri = ConfigExporter.class.getResource(resourceConfPath).toURI();
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                path = fileSystem.getPath(resourceConfPath);
            }
            else {
                path = Paths.get(uri);
            }

            // Copies all files inside the configuration root to the external root, if not already present
            for(Path p: Files.list(path).collect(Collectors.toList())) {
                if(madeDir || !Files.exists(Paths.get(extConfPath + p.getFileName().toString()))) {
                    Files.copy(p, Paths.get(extConfPath + p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
