package server;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YoolooSessionTest {
    @Test
    public void testDuplicateCard() {
        YoolooSession session = new YoolooSession(1, YoolooServer.GameMode.GAMEMODE_NULL, null);

        session.spieleKarteAus(0, 0, new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 1));
        assertEquals(session.spieleKarteAus(1, 0, new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 1)), false);
    }
}
