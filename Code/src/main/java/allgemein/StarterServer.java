// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package allgemein;

import server.YoolooServer;
import server.YoolooServer.GameMode;
import utils.PropertiesController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StarterServer {

    public static void main(String[] args) {
        Properties props = PropertiesController.getProperties("server");
        Logger logger = Logger.getAnonymousLogger();
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("logging.properties"));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        int listeningPort = props.get("server.port") != null ? Integer.parseInt(props.get("server.port").toString()) : 44137;
        int numOfPlayers = props.get("game.size") != null ? Integer.parseInt(props.get("game.size").toString()) : 2; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
        int botWait = props.get("game.bot.wait") != null ? Integer.parseInt(props.get("game.bot.wait").toString()) : 30;
        int minPlayers = props.get("game.min.players") != null ? Integer.parseInt(props.get("game.min.players").toString()) : 0;


        YoolooServer server = new YoolooServer(listeningPort, numOfPlayers, minPlayers, botWait, GameMode.GAMEMODE_SINGLE_GAME);
        server.startServer();
    }

}
