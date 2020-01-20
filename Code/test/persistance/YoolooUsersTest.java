package persistance;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import common.YoolooSpieler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class YoolooUsersTest {

    YoolooUsers users = new YoolooUsers();

    @BeforeEach
    void init() {
        users = new YoolooUsers();
    }

    @AfterEach
    void cleanup() {
        File file = new File("users.data");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void userNotExistantTest() {
        YoolooSpieler spieler = new YoolooSpieler("test", 10);
        YoolooKarte[] ref = new YoolooKarte[10];
        assertEquals(ref.length, users.getUserCardOrder(spieler).length);
        assertEquals(ref[0], users.getUserCardOrder(spieler)[0]);
    }

    @Test
    void addUserTest() {
        YoolooSpieler spieler = new YoolooSpieler("test", 1);
        YoolooKarte[] ref = {new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Blau, 99)};
        spieler.setAktuelleSortierung(ref);
        users.setUserCardOrder("test", new ArrayList<>(Arrays.asList(99)));
        assertEquals(ref.length, users.getUserCardOrder(spieler).length);
        assertEquals(ref[0].getWert(), users.getUserCardOrder(spieler)[0].getWert());
        assertEquals(ref[0].getFarbe(), users.getUserCardOrder(spieler)[0].getFarbe());
    }

}