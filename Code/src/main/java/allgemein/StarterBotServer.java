// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package allgemein;


import server.YoolooServer;
import server.YoolooServer.GameMode;
import server.bot.YoolooBotServer;

public class StarterBotServer {

    public static void main(String[] args) {
        int listeningPort = 44137;
        int spieleranzahl = 2; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
        YoolooServer server = new YoolooBotServer(listeningPort, spieleranzahl, GameMode.GAMEMODE_SINGLE_GAME);
        server.startServer();
    }

}
