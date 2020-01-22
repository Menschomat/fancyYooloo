package server;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YoolooSessionTest {
    @Test
    public void testDuplicateCard() {
        YoolooSession session = new YoolooSession(2, YoolooServer.GameMode.GAMEMODE_SINGLE_GAME, null);

        session.spieleKarteAus(0, 0, new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 1));
        session.spieleKarteAus(0, 1, new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 2));
        session.stichFuerRundeAuswerten(0);
        assertEquals(session.spieleKarteAus(1, 0, new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 1)), false);
    }
}
