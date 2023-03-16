package server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientTest {

    private static KVServer kvServer;
    private KVTaskClient taskClient;

    @BeforeAll
    static void load() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void start() throws IOException, InterruptedException {
        taskClient = new KVTaskClient(Constants.SERVER_URI);
    }

    @AfterAll
    static void stopAfterAll() {
        kvServer.stop();
    }

    @Test
    void shouldPutTaskAndLoadTask() {
        String key = this.getClass().getSimpleName() + Instant.now().getEpochSecond();
        assertThrows(IOException.class, () -> taskClient.load(null));
        assertThrows(IOException.class, () -> taskClient.load(key));
    }
}