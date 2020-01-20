package persistance;

import common.YoolooKarte;
import common.YoolooSpieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class YoolooUsersTest {

    YoolooUsers users = new YoolooUsers();

    @BeforeEach
    void init() {
        File file = new File("users.data");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void test() {
        YoolooSpieler spieler = new YoolooSpieler("test", 10);
        assertEquals(new YoolooKarte[10], users.getUserCardOrder(spieler));
    }

}