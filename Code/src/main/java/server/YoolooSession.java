// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package server;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import common.YoolooStich;
import server.YoolooServer.GameMode;

public class YoolooSession {

    private int anzahlSpielerInRunde;
    private GameMode gamemode = GameMode.GAMEMODE_NULL;
    private YoolooKarte[][] spielplan;
    private YoolooKartenspiel aktuellesSpiel;
    private YoolooStich[] ausgewerteteStiche;

    private YoolooServer server;

    public YoolooSession(int anzahlSpielerInRunde) {
        super();
        this.anzahlSpielerInRunde = anzahlSpielerInRunde;
        gamemode = GameMode.GAMEMODE_NULL;
        spielplan = new YoolooKarte[YoolooKartenspiel.maxKartenWert][anzahlSpielerInRunde];
        ausgewerteteStiche = new YoolooStich[YoolooKartenspiel.maxKartenWert];
        for (int i = 0; i < ausgewerteteStiche.length; i++) {
            ausgewerteteStiche[i] = null;
        }
        aktuellesSpiel = new YoolooKartenspiel();
        server = null;
    }

    public YoolooSession(int anzahlSpielerInRunde, GameMode gamemode, YoolooServer server) {
        this(anzahlSpielerInRunde);
        this.gamemode = gamemode;
        this.server = server;
    }

    public synchronized boolean spieleKarteAus(int stichNummer, int spielerID, YoolooKarte karte) {
        boolean ruleBroken = false;

        if (spielplan[spielerID][0] != null) {
            ruleBroken = spielplan[spielerID][0].getFarbe() != karte.getFarbe();
        }

        if (!ruleBroken) {
            for (int i = 0; i < spielplan[spielerID].length; i++) {
                if ((spielplan[spielerID][i] != null) && (spielplan[spielerID][i].getWert() == karte.getWert())) {
                    ruleBroken = true;
                }
            }
        }

        if (ruleBroken) {
            System.out.println("Spieler [ID: " + spielerID +  "] hat die Regeln nicht eingehalten, Runde wird abgebrochen!");
            if (server != null) {
                server.kickeAlleSpieler();
            }
        }

        spielplan[spielerID][stichNummer] = karte;

        return !ruleBroken;
    }

    public synchronized YoolooStich stichFuerRundeAuswerten(int stichNummer) {
        if (ausgewerteteStiche[stichNummer] == null) {
            YoolooStich neuerStich = null;
            YoolooKarte[] karten = spielplan[stichNummer];
            for (int spielernummer = 0; spielernummer < spielplan[stichNummer].length; spielernummer++) {
                if (spielplan[stichNummer][spielernummer] == null) {
                    karten = null;
                }
            }
            if (karten != null) {
                neuerStich = new YoolooStich(karten);
                neuerStich.setStichNummer(stichNummer);
                neuerStich.setSpielerNummer(aktuellesSpiel.berechneGewinnerIndex(karten));
                ausgewerteteStiche[stichNummer] = neuerStich;
                System.out.println("Stich ausgewertet:" + neuerStich.toString());
            }
        }
        return ausgewerteteStiche[stichNummer];

    }

    public synchronized YoolooKartenspiel getAktuellesSpiel() {
        return aktuellesSpiel;
    }

    public void setAktuellesSpiel(YoolooKartenspiel aktuellesSpiel) {
        this.aktuellesSpiel = aktuellesSpiel;
    }

    public int getAnzahlSpielerInRunde() {
        return anzahlSpielerInRunde;
    }

    public void setAnzahlSpielerInRunde(int anzahlSpielerInRunde) {
        this.anzahlSpielerInRunde = anzahlSpielerInRunde;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public YoolooKarte[][] getSpielplan() {
        return spielplan;
    }

    public void setSpielplan(YoolooKarte[][] spielplan) {
        this.spielplan = spielplan;
    }

    public String getErgebnis() {
        // TODO mit Funktion fuellen
        String ergebnis = "Ergebnis:\n blabla";
        return ergebnis;
    }

}
