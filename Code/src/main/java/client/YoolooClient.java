// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add  start_Client() SERVERMESSAGE_CHANGE_STATE 

package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import common.*;
import messages.ClientMessage;
import messages.ClientMessage.ClientMessageType;
import messages.ServerMessage;
import utils.PropertiesController;


public class YoolooClient {
    private String serverHostname = "localhost";
    private int serverPort = 44137;
    private Socket serverSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    private ClientState clientState = ClientState.CLIENTSTATE_NULL;

    private String spielerName = "Name" + (System.currentTimeMillis() + "").substring(6);
    private LoginMessage newLogin = null;
    private YoolooSpieler meinSpieler;
    private YoolooStich[] spielVerlauf = null;

    private boolean nameCheck = false;

    Logger logger = PropertiesController.getLogger(YoolooClient.class.getName());

    public YoolooClient() {
        super();
    }

    public YoolooClient(String serverHostname, int serverPort, boolean nameCheck) {
        super();
        this.serverPort = serverPort;
        clientState = ClientState.CLIENTSTATE_NULL;
        this.nameCheck = nameCheck;
    }

    /**
     * Client arbeitet statusorientiert als Kommandoempfuenger in einer Schleife.
     * Diese terminiert wenn das Spiel oder die Verbindung beendet wird.
     */
    public void startClient() {

        try {
            clientState = ClientState.CLIENTSTATE_CONNECT;
            verbindeZumServer();

            while (clientState != ClientState.CLIENTSTATE_DISCONNECTED && ois != null && oos != null) {
                // 1. Schritt Kommado empfangen
                ServerMessage kommandoMessage = empfangeKommando();
                logger.fine("[id-x]ClientStatus: " + clientState + "] " + kommandoMessage.toString());
                // 2. Schritt ClientState ggfs aktualisieren (fuer alle neuen Kommandos)
                ClientState newClientState = kommandoMessage.getNextClientState();
                if (newClientState != null) {
                    clientState = newClientState;
                }
                // 3. Schritt Kommandospezifisch reagieren
                switch (kommandoMessage.getServerMessageType()) {
                    case SERVERMESSAGE_SENDLOGIN:
                        // Server fordert Useridentifikation an
                        // Falls User local noch nicht bekannt wird er bestimmt
                        if (newLogin == null || clientState == ClientState.CLIENTSTATE_LOGIN) {
                            // TODO Klasse LoginMessage erweiteren um Interaktives ermitteln des
                            // Spielernames, GameModes, ...)
                            newLogin = eingabeSpielerDatenFuerLogin(); //Dummy aufruf
                            newLogin = new LoginMessage(spielerName);
                        }
                        // Client meldet den Spieler an den Server
                        oos.writeObject(newLogin);
                        logger.fine("[id-x]ClientStatus: " + clientState + "] : LoginMessage fuer  " + spielerName
                                + " an server gesendet warte auf Spielerdaten");
                        empfangeSpieler();
                        // ausgabeKartenSet();
                        break;
                    case SERVERMESSAGE_SORT_CARD_SET:
                        // sortieren Karten
                        meinSpieler.setAktuelleSortierung(fancySortierung());
                        ausgabeKartenSet();
                        // ggfs. Spielverlauf löschen
                        spielVerlauf = new YoolooStich[YoolooKartenspiel.maxKartenWert];
                        ClientMessage message = new ClientMessage(ClientMessageType.ClientMessage_OK,
                                "Kartensortierung ist erfolgt!");
                        oos.writeObject(message);
                        break;
                    case SERVERMESSAGE_SEND_CARD:
                        spieleStich(kommandoMessage.getParamInt());
                        break;
                    case SERVERMESSAGE_RESULT_SET:
                        logger.fine("[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState
                                + "] : Ergebnis ausgeben ");
                        String ergebnis = empfangeErgebnis();
                        System.out.println(ergebnis.toString());
                        break;
                    // basic version: wechsel zu ClientState Disconnected thread beenden
                    case SERVERMESSAGE_CHANGE_STATE:
                        break;

                    default:
                        break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verbindung zum Server aufbauen, wenn Server nicht antwortet nach ein Sekunde
     * nochmals versuchen
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    // TODO Abbruch nach x Minuten einrichten
    private void verbindeZumServer() throws UnknownHostException, IOException {
        while (serverSocket == null) {
            try {
                serverSocket = new Socket(serverHostname, serverPort);
            } catch (ConnectException e) {
                logger.warning("Server antwortet nicht - ggfs. neu starten");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                }
            }
        }
        logger.fine("[Client] Serversocket eingerichtet: " + serverSocket.toString());
        // Kommunikationskanuele einrichten
        ois = new ObjectInputStream(serverSocket.getInputStream());
        oos = new ObjectOutputStream(serverSocket.getOutputStream());
        if (nameCheck) {
            try {
                logger.fine("Spielerprüfung erfolgt. Übermittle Spielernamen");
                oos.writeObject(this.spielerName);
                ClientState answer = null;
                while (answer == null) {
                    answer = (ClientState) ois.readObject();
                }
                if (answer.equals(ClientState.CLIENTSTATE_DISCONNECTED)) {
                    clientState = answer;
                    logger.fine("Server hat Verbindung verweigert. Schließe Client.");
                }
            } catch (ClassNotFoundException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private void spieleStich(int stichNummer) throws IOException {
        System.out.println("[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState
                + "] : Spiele Karte " + stichNummer);
        spieleKarteAus(stichNummer);
        YoolooStich iStich = empfangeStich();
        spielVerlauf[stichNummer] = iStich;
        System.out.println("[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState
                + "] : Empfange Stich " + iStich);
        if (iStich.getSpielerNummer() == meinSpieler.getClientHandlerId()) {
            System.out.print(
                    "[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState + "] : Gewonnen - ");
            meinSpieler.erhaeltPunkte(iStich.getStichNummer() + 1);
        }

    }

    private void spieleKarteAus(int i) throws IOException {
        oos.writeObject(meinSpieler.getAktuelleSortierung()[i]);
    }

    // Methoden fuer Datenempfang vom Server / ClientHandler
    private ServerMessage empfangeKommando() {
        ServerMessage kommando = null;
        boolean failed = false;
        try {
            kommando = (ServerMessage) ois.readObject();
        } catch (ClassNotFoundException e) {
            failed = true;
            e.printStackTrace();
        } catch (IOException e) {
            failed = true;
            e.printStackTrace();
        }
        if (failed)
            kommando = null;
        return kommando;
    }

    private void empfangeSpieler() {
        try {
            meinSpieler = (YoolooSpieler) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private YoolooStich empfangeStich() {
        try {
            return (YoolooStich) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String empfangeErgebnis() {
        try {
            return (String) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LoginMessage eingabeSpielerDatenFuerLogin() {
        // TODO Spielername, GameMode und ggfs mehr ermitteln
        return null;
    }

    public void ausgabeKartenSet() {
        // Ausgabe Kartenset
        logger.fine("[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState
                + "] : Uebermittelte Kartensortierung beim Login ");
        for (int i = 0; i < meinSpieler.getAktuelleSortierung().length; i++) {
            System.out.println("[id-" + meinSpieler.getClientHandlerId() + "]ClientStatus: " + clientState
                    + "] : Karte " + (i + 1) + ":" + meinSpieler.getAktuelleSortierung()[i]);
        }

    }

    public enum ClientState {
        CLIENTSTATE_NULL, // Status nicht definiert
        CLIENTSTATE_CONNECT, // Verbindung zum Server wird aufgebaut
        CLIENTSTATE_LOGIN, // Anmeldung am Client Informationen des Users sammeln
        CLIENTSTATE_RECEIVE_CARDS, // Anmeldung am Server
        CLIENTSTATE_SORT_CARDS, // Anmeldung am Server
        CLIENTSTATE_REGISTER, // t.b.d.
        CLIENTSTATE_PLAY_SINGLE_GAME, // Spielmodus einfaches Spiel
        CLIENTSTATE_DISCONNECT, // Verbindung soll getrennt werden
        CLIENTSTATE_DISCONNECTED // Vebindung wurde getrennt
    }

    public YoolooKarte[] sortierungFestlegen() {
        YoolooKarte[] neueSortierung = new YoolooKarte[this.meinSpieler.getAktuelleSortierung().length];
        for (int i = 0; i < neueSortierung.length; i++) {
            int neuerIndex = (int) (Math.random() * neueSortierung.length);
            while (neueSortierung[neuerIndex] != null) {
                neuerIndex = (int) (Math.random() * neueSortierung.length);
            }
            neueSortierung[neuerIndex] = meinSpieler.getAktuelleSortierung()[i];
            // System.out.println(i+ ". neuerIndex: "+neuerIndex);
        }
        return neueSortierung;
    }

    public YoolooKarte[] fancySortierung() {
        YoolooKarte[] fancySortierung = new YoolooKarte[this.meinSpieler.getAktuelleSortierung().length];
        Random random = new Random();
        int asdf = random.nextInt(2);
        switch (asdf) {
            case 0:
                fancySortierung = sortierungFestlegen();
                logger.log(Level.INFO, "Karten wurden Random sortiert.");
                break;
            case 1:
                for (int i = 0; i < fancySortierung.length; i++) {
                    fancySortierung[i] = meinSpieler.getAktuelleSortierung()[i];
                }
                logger.log(Level.INFO, "Karten wurde von klein nach groß sortiert.");
                break;
            case 2:
                for (int i = 0; i < fancySortierung.length; i++) {
                    fancySortierung[i] = meinSpieler.getAktuelleSortierung()[meinSpieler.getAktuelleSortierung().length - 1 - i];
                }
                logger.log(Level.INFO, "Karten wurde von groß nach klein sortiert.");
                break;
        }
        return fancySortierung;
    }

    public void setName(String spielerName) {
        this.spielerName = spielerName;
    }

}
