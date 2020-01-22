package server;

import org.junit.jupiter.api.Test;

import java.net.Socket;

public class YoolooClientHandlerTest {
    @Test
    void testDuplicateCards() {
        YoolooServer server = new YoolooServer(12345, 2, 0, 10, YoolooServer.GameMode.GAMEMODE_NULL);
        YoolooClientHandler handler = new YoolooClientHandler(server, new Socket());
        //handler.spie
    }
}
