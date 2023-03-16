package server;

import controls.Managers;
import controls.TaskManager;
import com.google.gson.Gson;
import tasks.TaskStatus;
import tasks.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

import static server.Constants.*;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static HttpClient httpClient;
    private HttpTaskServer taskServer;
    private static KVServer kvServer;
    private Task task;

    @BeforeAll
    static void load() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @BeforeEach
    void start() throws IOException {
        taskServer = new HttpTaskServer();
        taskServer.startHttpTaskServer();
        TaskManager taskManager = Managers.getDefault();
        task = new Task(
                "taskTitle",
                "taskDescription",
                TaskStatus.NEW,
                Instant.now(),
                Duration.ofMinutes(15)
        );
        taskManager.addTask(task);
        assertNotNull(task);
        assertNotNull(taskServer);
        assertNotNull(taskManager);
    }

    @AfterEach
    void stop() {
        taskServer.stopHttpTaskServer();
    }

    @AfterAll
    static void stopAfterAll() {
        kvServer.stop();
    }

    @Test
    void testPutOrDeleteRequests() throws IOException, InterruptedException {
        HttpResponse<String> response;

        Gson gson = new Gson();
        response = httpClient.send(HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_TASK))
                        .build()
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_OK, response.statusCode(), response.body());

        response = httpClient.send(HttpRequest.newBuilder()
                        .DELETE()
                        .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_TASK + "/12"))
                        .build()
                , HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_OK, response.statusCode());
    }

    @Test
    void testGetRequests() throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_TASK + "1919191"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_SUBTASK + "1919191"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_EPIC + "1919191"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_TASK + "strangeId"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_SUBTASK + "strangeId"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + Constants.PORT + TASKS_EPIC + "strangeId"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_BAD_REQUEST, response.statusCode());
    }
}