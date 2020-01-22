package client;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class YoolooClientTest {

    YoolooClient yoolooClient = new YoolooClient();
    YoolooKarte[] yoolooKartes = new YoolooKarte[10];

    @BeforeAll
    void createValues() {
        for (int i = 1; i <= 10; i++) {
            YoolooKarte yoolooKarte = new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Rot, i);
            yoolooKartes[i - 1] = yoolooKarte;
        }
        yoolooClient.setSpieler(yoolooKartes);
    }

    @Test
    void startClient() {
        assertEquals(true, false);
    }

    @Test
    void ausgabeKartenSet() {
        assertEquals(true, false);
    }

    @Test
    void testFancySortierung() {
        YoolooKarte[] afterSort = yoolooClient.fancySortierung();
        assertEquals(afterSort, yoolooKartes);
    }
}