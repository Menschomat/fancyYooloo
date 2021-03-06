// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change 

package server;

import client.YoolooClient;
import common.YoolooKartenspiel;
import persistance.YoolooUsers;
import utils.PropertiesController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class YoolooServer {

    // Server Standardwerte koennen ueber zweite Konstruktor modifiziert werden!
    private int port = 44137;
    private int spielerProRunde = 8; // min 1, max Anzahl definierte Farben in Enum YoolooKartenSpiel.KartenFarbe)
    private int minRealPlayers = 0;
    private int waitForPlayers = 10; //seconds to wait befor bot-Spawn
    private boolean botSpawnerRunning = false;
    private GameMode serverGameMode = GameMode.GAMEMODE_SINGLE_GAME;
    private YoolooUsers users = new YoolooUsers();
    private Logger logger = PropertiesController.getLogger("YoolooServer");
    private ServerSocket serverSocket = null;
    private boolean serverAktiv = true;
    private boolean checkName = false;

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

    public YoolooServer(int port, int spielerProRunde, int minRealPlayers, int waitForPlayers, GameMode gameMode, boolean checkName) {
        this.port = port;
        this.spielerProRunde = spielerProRunde;
        this.serverGameMode = gameMode;
        this.minRealPlayers = minRealPlayers;
        this.waitForPlayers = waitForPlayers;
        this.checkName = checkName;
    }

    public YoolooServer() {

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
            logger.fine("Server gestartet - warte auf Spieler");
            Set<String> playerNames = new HashSet<>();
            while (serverAktiv) {
                // Neue Spieler registrieren
                if (!botSpawnerRunning)
                    spawnBots();
                try {
                    Socket client = serverSocket.accept();
                    boolean addClient = true;
                    if (checkName) {
                        logger.fine("Prüfe ob Spielername bereits verbunden");
                        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                        String playerName = (String) ois.readObject();
                        if (playerNames.contains(playerName)) {
                            addClient = false;
                            oos.writeObject(YoolooClient.ClientState.CLIENTSTATE_DISCONNECTED);
                            client.close();
                            logger.warning("Spieler bereits verbunden. Breche Verbindungsversuch ab.");
                        } else {
                            oos.writeObject(YoolooClient.ClientState.CLIENTSTATE_CONNECT);
                            playerNames.add(playerName);
                            logger.fine("Spieler noch nicht verbunden. Füge Spieler hinzu.");
                        }
                    }

                    if (addClient) {
                        YoolooClientHandler clientHandler = new YoolooClientHandler(this, client);
                        clientHandlerList.add(clientHandler);
                        logger.fine("[YoolooServer] Anzahl verbundene Spieler: " + clientHandlerList.size());
                    }
                } catch (IOException e) {
                    logger.severe("Client Verbindung gescheitert");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    logger.severe(e.getMessage());
                    e.printStackTrace();
                }
                // Neue Session starten wenn ausreichend Spieler verbunden sind!
                if (clientHandlerList.size() >= Math.min(spielerProRunde, YoolooKartenspiel.Kartenfarbe.values().length)) {
                    // Init Session
                    YoolooSession yoolooSession = new YoolooSession(clientHandlerList.size(), serverGameMode, this);

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
                    playerNames = new HashSet<>();
                }
                if (Thread.currentThread().isInterrupted()) {
                    shutDownServer(543210);
                }
            }
        } catch (IOException e) {
            logger.severe("ServerSocket nicht gebunden");
            serverAktiv = false;
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
                            int target = spielerProRunde - yoolooServer.getClientCount();
                            for (int i = 0; i < target; i++) {
                                logger.info("SPAWNING BOT" + i);
                                spielerPool.execute(() -> new YoolooClient().startClient());
                                Thread.sleep(100);
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
    public void shutDownServer(int code) throws IOException {
        if (code == 543210) {
            this.serverAktiv = false;
            logger.fine("Server wird beendet");
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void kickeAlleSpieler() {
        clientHandlerList.forEach((n) -> n.kickClient());
    }
}
