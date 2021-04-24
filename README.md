# Software engineering project 2020
## Group AM27

- [@rb-sl](https://github.com/rb-sl)
- [@Davibia](https://github.com/Davibia)
- [@Erica_c](https://github.com/ericaceriotti)

## The game
The project consists in the Java implementation of the board game [Santorini](https://roxley.com/products/santorini) by Spin Master.

All copyrights on ideas and image resources belong to Spin Master and Roxley Games.
 
## Developed functionalities
| Functionality  | State  |
| :------------- | :----: |
| Basic rules    | 🟩 |
| Complete rules | 🟩 |
| Socket         | 🟩 |
| GUI            | 🟩 |
| CLI            | 🟩 |
| Multiple games |    |
| Persistence    |     |
| Advanced Gods  | 🟩 |
| Undo           | 🟩 |

## Exam and results
During the final examination the instructors asked some clarifications about the heartbeat system,
but in the end enthusiastically accepted the design choices.

This project was evaluated with 30L (30 e lode).

## Software and tools
This is the list of software used during the development of the project:
* StarUML: UML design
* IntelliJ IDEA: Java IDE
* Scene Builder: Designer for JavaFX
* Blender, Photoshop: for creating the icons from the given models
* TeXStudio and MikTeX: LaTeX tools to write the communication document
* Inkscape: for creating images in the communication document

## Running the tests
The project's tests can be run either from Maven (through the junit plugin) or from IntelliJ's tests folder. 
Please note that the randomGamesTest runs a given number of matches for all the god combinations and generates statistics, so it might take 
some time. 

## Deliverables folder
Inside the deliverables folder can be found:
* The project's JavaDoc
* The coverage report for model, view and controller classes (limited to a local execution)
* The initial and final model UMLs
* The documentation regarding the adopted communication protocol
* The project's jars
* The final presentation

### Using the jars
In the dedicated folder there are both the server and client jars.<br>
To start the server, simply run from a console
```$xslt
java -jar SantoriniServer.jar
```
For the client, the application can be started specifying a number of parameters in any order, e.g.
```$xslt
java -jar SantoriniClient.jar 127.0.0.1 49153 cli
```
Omitting the server's ip address and port to specify just the type of user interface (cli or gui) will make the application read the parameters given 
in the configuration file ClientConfigs.json, while launching the game without parameters will default to the gui version (accessible by just 
double-clicking on the application's jar too).<br>
If using the cli version from windows, please launch the application on the linux subsystem.

The configuration files are exported to the working directory at the first execution, 
in order to allow the user to modify some game parameters (some changes may need to be reflected 
between client and server configurations).

#### Jar generation
The jar files have been generated through Maven's assembly plugin; after choosing the version to create through the pom.xml file,
the module is run (The newly generated jar can be found in the target folder).
