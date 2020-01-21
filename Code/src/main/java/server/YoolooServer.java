// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change 

package server;

import client.YoolooClient;
import common.YoolooKartenspiel;
import persistance.YoolooUsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class YoolooServer {

    // Server Standardwerte koennen ueber zweite Konstruktor modifiziert werden!
    private int port = 44137;
    private int spielerProRunde = 8; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
    private int minRealPlayers = 1;
    private int waitForPlayers = 10; //seconds to wait befor bot-Spawn
    private boolean botSpawnerRunning = false;
    private GameMode serverGameMode = GameMode.GAMEMODE_SINGLE_GAME;
    private YoolooUsers users = new YoolooUsers();
    private Logger logger = Logger.getLogger("YoolooServer");
    private ServerSocket serverSocket = null;
    private boolean serverAktiv = true;

    // private ArrayList<Thread> spielerThreads;
    private ArrayList<YoolooClientHandler> clientHandlerList = new ArrayList<>();

    private ExecutorService spielerPool = Executors.newCachedThreadPool();

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
            logger.info("Server gestartet - warte auf Spieler");
            while (serverAktiv) {
                // Neue Spieler registrieren
                if (!botSpawnerRunning)
                    spawnBots();
                try {
                    Socket client = serverSocket.accept();
                    YoolooClientHandler clientHandler = new YoolooClientHandler(this, client);
                    clientHandlerList.add(clientHandler);
                    logger.info("[YoolooServer] Anzahl verbundene Spieler: " + clientHandlerList.size());
                } catch (IOException e) {
                    logger.warning("Client Verbindung gescheitert");
                    e.printStackTrace();
                }
                // Neue Session starten wenn ausreichend Spieler verbunden sind!
                if (clientHandlerList.size() >= Math.min(spielerProRunde, YoolooKartenspiel.Kartenfarbe.values().length)) {
                    // Init Session
                    YoolooSession yoolooSession = new YoolooSession(clientHandlerList.size(), serverGameMode);

                    // Starte pro Client einen ClientHandlerTread
                    for (int i = 0; i < clientHandlerList.size(); i++) {
                        YoolooClientHandler ch = clientHandlerList.get(i);
                        ch.setHandlerID(i);
                        ch.joinSession(yoolooSession);
                        spielerPool.execute(ch); // Start der ClientHandlerThread - Aufruf der Methode run()
                    }

                    // nächste Runde eröffnen
                    clientHandlerList = new ArrayList<YoolooClientHandler>();
                    botSpawnerRunning = false;
                }
            }
        } catch (IOException e1) {
            System.out.println("ServerSocket nicht gebunden");
            serverAktiv = false;
            e1.printStackTrace();
        }

    }

    private void spawnBots() {
        botSpawnerRunning = true;
        spielerPool.execute(new Runnable() {
            private YoolooServer yoolooServer;

            public Runnable init(YoolooServer yoolooServer) {
                this.yoolooServer = yoolooServer;
                return (this);
            }

            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                while (yoolooServer.getClientCount() < yoolooServer.getSpielerProRunde()) {
                    try {
                        if (yoolooServer.getClientCount() < minRealPlayers) {
                            logger.info("Zu wenige Spieler für einen Bot-Spawn");
                            startTime = System.currentTimeMillis();
                            Thread.sleep(3000);
                            continue;
                        }
                        logger.info("Bot-Spawn in " + (waitForPlayers - ((System.currentTimeMillis() / 1000) - startTime / 1000)) + "s");
                        if (yoolooServer.botSpawnCriteriaOk(startTime)) {
                            for (int i = 1; i == spielerProRunde - yoolooServer.getClientCount(); i++) {
                                logger.info("SPAWNING BOT" + i);
                                spielerPool.execute(() -> new YoolooClient().startClient());
                            }
                            Thread.currentThread().interrupt();
                            return;
                        }

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.init(this));
    }

    private boolean botSpawnCriteriaOk(long startedSearchTime) {

        return clientSpawnTimeExceeded(startedSearchTime)
                && clientHandlerList.size() >= minRealPlayers
                && clientHandlerList.size() != spielerProRunde;
    }

    private boolean clientSpawnTimeExceeded(long startedSearch) {
        return ((System.currentTimeMillis() / 1000) - startedSearch / 1000) > waitForPlayers;
    }


    // TODO Dummy zur Serverterminierung noch nicht funktional
    public void shutDownServer(int code) {
        if (code == 543210) {
            this.serverAktiv = false;
            logger.info("Server wird beendet");
            spielerPool.shutdown();
        } else {
            logger.warning("Servercode falsch");
        }
    }

    public YoolooUsers getUsers() {
        return users;
    }

    public int getClientCount() {
        return clientHandlerList.size();
    }

    public int getSpielerProRunde() {
        return spielerProRunde;
    }

    public GameMode getServerGameMode() {
        return serverGameMode;
    }

    public void setServerGameMode(GameMode serverGameMode) {
        this.serverGameMode = serverGameMode;
    }
}
