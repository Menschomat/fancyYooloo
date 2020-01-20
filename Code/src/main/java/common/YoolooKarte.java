// History of Change
// vernr    |date  | who | lineno | what
//  V0.106  |200107| cic |    -   | add history of change

package common;

import java.io.Serializable;

import common.YoolooKartenspiel.Kartenfarbe;

public class YoolooKarte implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8114061865027839486L;
    private Kartenfarbe farbe;
    private int wert;

    public YoolooKarte(Kartenfarbe kartenfarbe, int kartenwert) {
        this.farbe = kartenfarbe;
        this.wert = kartenwert;
    }

    public Kartenfarbe getFarbe() {
        return farbe;
    }

    public int getWert() {
        return wert;
    }

    @Override
    public String toString() {
        return "YoolooKarte [farbe=" + farbe + ", wert=" + wert + "]";
    }

}
