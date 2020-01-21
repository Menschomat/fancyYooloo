package server;

import java.net.Socket;

public class YoolooClientHandlerTest {
    @Test
    void testDuplicateCards() {
        YoolooServer server = new YoolooServer(12345, 2, YoolooServer.GameMode.GAMEMODE_NULL);
        YoolooClientHandler handler = new YoolooClientHandler(server, new Socket());

        //handler.spie
    }
}
