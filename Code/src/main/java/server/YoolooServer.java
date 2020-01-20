// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change 

package server;

import common.YoolooKartenspiel;
import persistance.YoolooUsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class YoolooServer {

    // Server Standardwerte koennen ueber zweite Konstruktor modifiziert werden!
    private int port = 44137;
    private int spielerProRunde = 8; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
    private GameMode serverGameMode = GameMode.GAMEMODE_SINGLE_GAME;
    private YoolooUsers users = new YoolooUsers();

    public GameMode getServerGameMode() {
        return serverGameMode;
    }

    private Logger logger = Logger.getLogger("YoolooServer");

    public void setServerGameMode(GameMode serverGameMode) {
        this.serverGameMode = serverGameMode;
    }

    private ServerSocket serverSocket = null;
    private boolean serverAktiv = true;

    // private ArrayList<Thread> spielerThreads;
    private ArrayList<YoolooClientHandler> clientHandlerList = new ArrayList<>();

    private ExecutorService spielerPool;

    /**
     * Serverseitig durch ClientHandler angebotenen SpielModi. Bedeutung der
     * einzelnen Codes siehe Inlinekommentare.
     * <p>
     * Derzeit nur Modus Play Single Game genutzt
     */
    public enum GameMode {
        GAMEMODE_NULL, // Spielmodus noch nicht definiert
        GAMEMODE_SINGLE_GAME, // Spielmodus: einfaches Spiel
        GAMEMODE_PLAY_ROUND_GAME, // noch nicht genutzt: Spielmodus: Eine Runde von Spielen
        GAMEMODE_PLAY_LIGA, // noch nicht genutzt: Spielmodus: Jeder gegen jeden
        GAMEMODE_PLAY_POKAL, // noch nicht genutzt: Spielmodus: KO System
        GAMEMODE_PLAY_POKAL_LL // noch nicht genutzt: Spielmodus: KO System mit Lucky Looser
    }

    ;

    public YoolooServer(int port, int spielerProRunde, GameMode gameMode) {
        this.port = port;
        this.spielerProRunde = spielerProRunde;
        this.serverGameMode = gameMode;
    }

    private void printBanner() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(
                getClass().getClassLoader().getResource("banner.txt").getFile()
        )))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            logger.warning("No banner was found!");
        }

    }

    public void startServer() {
        printBanner();
        try {
            // Init
            serverSocket = new ServerSocket(port);
            spielerPool = Executors.newCachedThreadPool();
            System.out.println("Server gestartet - warte auf Spieler");

            while (serverAktiv) {
                Socket client = null;

                // Neue Spieler registrieren
                try {
                    client = serverSocket.accept();
                    YoolooClientHandler clientHandler = new YoolooClientHandler(this, client);
                    clientHandlerList.add(clientHandler);
                    System.out.println("[YoolooServer] Anzahl verbundene Spieler: " + clientHandlerList.size());
                } catch (IOException e) {
                    System.out.println("Client Verbindung gescheitert");
                    e.printStackTrace();
                }

                // Neue Session starten wenn ausreichend Spieler verbunden sind!
                if (clientHandlerList.size() >= Math.min(spielerProRunde,
                        YoolooKartenspiel.Kartenfarbe.values().length)) {
                    // Init Session
                    YoolooSession yoolooSession = new YoolooSession(clientHandlerList.size(), serverGameMode);

                    // Starte pro Client einen ClientHandlerTread
                    for (int i = 0; i < clientHandlerList.size(); i++) {
                        YoolooClientHandler ch = clientHandlerList.get(i);
                        ch.setHandlerID(i);
                        ch.joinSession(yoolooSession);
                        spielerPool.execute(ch); // Start der ClientHandlerThread - Aufruf der Methode run()
                    }

                    // nuechste Runde eroeffnen
                    clientHandlerList = new ArrayList<YoolooClientHandler>();
                }
            }
        } catch (IOException e1) {
            System.out.println("ServerSocket nicht gebunden");
            serverAktiv = false;
            e1.printStackTrace();
        }

    }

    public YoolooUsers getUsers() {
        return users;
    }

    public int getClientCount() {
        return clientHandlerList.size();
    }

    // TODO Dummy zur Serverterminierung noch nicht funktional
    public void shutDownServer(int code) {
        if (code == 543210) {
            this.serverAktiv = false;
            System.out.println("Server wird beendet");
            spielerPool.shutdown();
        } else {
            System.out.println("Servercode falsch");
        }
    }
}
