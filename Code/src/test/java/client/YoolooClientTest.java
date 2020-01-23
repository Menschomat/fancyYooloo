package client;

import common.YoolooKarte;
import common.YoolooKartenspiel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YoolooClientTest {
  /* static YoolooClient yoolooClient;

    @BeforeAll
    static void createData(){
        yoolooClient = new YoolooClient();
        yoolooClient.createSpieler();

    }


    @Test
    void testFancySortierungKleinGroß(){
        YoolooKarte[] sortierung = yoolooClient.fancySortierung("testClientKleinGroß");
        YoolooKarte[] erwarteteSortierung = new YoolooKarte[10];
        for(int i = 0; i < 10; i ++){
            YoolooKarte yoolooKarte = new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Rot, i + 1);
            erwarteteSortierung[i] = yoolooKarte;
        }
        for(int i = 0; i < 10; i++) {
            assertEquals(erwarteteSortierung[i].getWert(), sortierung[i].getWert());
            assertEquals(erwarteteSortierung[i].getFarbe(), sortierung[i].getFarbe());
        }
    }

    @Test
    void testFancySortierungGroßKlein(){
        YoolooKarte[] sortierung = yoolooClient.fancySortierung("testClientGroßKlein");
        YoolooKarte[] erwarteteSortierung = new YoolooKarte[10];
        for(int i = 0; i < 10; i ++){
            YoolooKarte yoolooKarte = new YoolooKarte(YoolooKartenspiel.Kartenfarbe.Rot, sortierung.length - i);
            erwarteteSortierung[i] = yoolooKarte;
        }
        for(int i = 0; i < 10; i++) {
            assertEquals(erwarteteSortierung[i].getWert(), sortierung[i].getWert());
            assertEquals(erwarteteSortierung[i].getFarbe(), sortierung[i].getFarbe());
        }
    }*/
}