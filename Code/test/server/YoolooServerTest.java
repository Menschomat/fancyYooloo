package server;

import client.YoolooClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class YoolooServerTest {

    /* @Test
     void getServerGameMode() {
         assertEquals(true, false);
     }

     @Test
     void setServerGameMode() {
         assertEquals(true, false);
     }

     @Test
     void startServer() {
         assertEquals(true, false);
     }

     @Test
     void shutDownServer() {
         assertEquals(true, false);
     } */
    @Test
    void clientConnectionTest() throws InterruptedException, ExecutionException {
        YoolooServer server = new YoolooServer(44137, 2, 2, 0, YoolooServer.GameMode.GAMEMODE_SINGLE_GAME);
        YoolooClient client1 = new YoolooClient();
        YoolooClient client2 = new YoolooClient();
        final ExecutorService service = Executors.newFixedThreadPool(22);

        service.execute(() -> server.startServer());
        final Future<Integer> result = service.submit(() -> maxClientWatcher(3000, server));
        service.execute(() -> client1.startClient());
        service.execute(() -> client2.startClient());
        while (!result.isDone()) {
            Thread.sleep(300);
        }
        service.shutdownNow();
        assertEquals(2, result.get());
    }

    @Test
    void botSpawnTest() throws InterruptedException, ExecutionException {
        YoolooServer server = new YoolooServer(44137, 4, 0, 0, YoolooServer.GameMode.GAMEMODE_SINGLE_GAME);
        final ExecutorService service = Executors.newFixedThreadPool(22);
        service.execute(() -> server.startServer());
        final Future<Integer> result = service.submit(() -> maxClientWatcher(1500, server));
        while (!result.isDone()) {
            Thread.sleep(300);
        }
        service.shutdownNow();
        assertEquals(4, result.get());
    }

    private int maxClientWatcher(long mils, YoolooServer server) {
        System.out.println(server);
        int max_count = 0;
        long start_mils = System.currentTimeMillis();
        while (System.currentTimeMillis() - start_mils < mils) {
            int cur_count = server.getClientCount();
            max_count = cur_count > max_count ? cur_count : max_count;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return max_count;
    }

    @AfterEach
    void waitSomeTime() throws InterruptedException {
        Thread.sleep(4000);
    }
}
