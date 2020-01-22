// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package allgemein;

import server.YoolooServer;
import server.YoolooServer.GameMode;
import utils.PropertiesController;

import java.util.Properties;

public class StarterServer {

    public static void main(String[] args) throws InterruptedException {
        Properties props = PropertiesController.getProperties("server");
        int listeningPort = props.get("server.port") != null ? Integer.parseInt(props.get("server.port").toString()) : 44137;
        int numOfPlayers = props.get("game.size") != null ? Integer.parseInt(props.get("game.size").toString()) : 2; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
        int botWait = props.get("game.bot.wait") != null ? Integer.parseInt(props.get("game.bot.wait").toString()) : 30;
        int minPlayers = props.get("game.min.players") != null ? Integer.parseInt(props.get("game.min.players").toString()) : 0;


        YoolooServer server = new YoolooServer(listeningPort, numOfPlayers, minPlayers, botWait, GameMode.GAMEMODE_SINGLE_GAME);
        server.startServer();
    }

}
