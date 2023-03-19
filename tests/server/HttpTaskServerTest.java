package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import controls.HTTPTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    protected KVServer kvServer;
    protected HttpTaskServer taskServer;
    protected HttpClient client;
    protected Gson gson;

    protected Task task1;
    protected Task task2;
    protected Task task3;

    protected Epic epic1;
    protected Epic epic2;
    protected SubTask subtask1;
    protected SubTask subtask2;

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = HTTPTaskManager.getTaskGson();
        initTasks();
    }

    protected void initTasks() {
        task1 = new Task("Task 1", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H110M"),
                LocalDateTime.parse("11.58.08_07.2023", Task.getTaskTimeFormatter())
        );
        task2 = new Task("Task 2", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H90M"),
                LocalDateTime.parse("09.58.08_06.2023", Task.getTaskTimeFormatter())
        );
        task3 = new Task("Task 3", "Task Description 1", TaskStatus.NEW, Duration.parse("PT0H60M"),
                LocalDateTime.parse("10.58.08_05.2023", Task.getTaskTimeFormatter())
        );
        epic1 = new Epic("Epic 1", "Epic Description 1", TaskStatus.NEW);
        epic2 = new Epic("Epic 2", "Epic Description 1", TaskStatus.NEW);
        subtask1 = new SubTask("SubTask 1", "SubTask Description 1", TaskStatus.NEW, Duration.parse("PT0H20M"),
                LocalDateTime.parse("14.58.08_04.2023", Task.getTaskTimeFormatter()), epic1.getTaskKey()
        );
        subtask2 = new SubTask("SubTask 2", "SubTask Description 1", TaskStatus.NEW, Duration.parse("PT0H15M"),
                LocalDateTime.parse("13.58.08_04.2023", Task.getTaskTimeFormatter()), epic1.getTaskKey()
        );
    }

    @Test
    void shouldEndPointTaskCreate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200.");

        url = URI.create("http://localhost:8080/tasks/task/?taskKey=1");
        json = getTask(url).body();
        Task task = gson.fromJson(json, Task.class);
        assertEquals(task.getTaskDescription(), task1.getTaskDescription(), "Задача не соответствует ожидаемой.");

        url = URI.create("http://localhost:8080/tasks/task/");
        json = getTask(url).body();
        ArrayList<Task> tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertEquals(tasks.size(), 1, "Количество задач не соответствует ожидаемому.");

        url = URI.create("http://localhost:8080/tasks/task/?taskKey=1");
        assertEquals(removeTask(url).statusCode(), 200, "Вернулся код отличный от 200");
    }

    @Test
    void shouldEndPointEpicCreate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        url = URI.create("http://localhost:8080/tasks/epic/?taskKey=1");
        json = getTask(url).body();
        Epic epic = gson.fromJson(json, Epic.class);
        assertEquals(epic.getTaskDescription(), epic1.getTaskDescription(), "Задача не соответствует ожидаемой.");

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = getTask(url).body();
        ArrayList<Epic> tasks = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {

        }.getType());
        assertEquals(tasks.size(), 1, "Количество задач не соответствует ожидаемому.");

        url = URI.create("http://localhost:8080/tasks/epic/?taskKey=1");
        assertEquals(removeTask(url).statusCode(), 200, "Вернулся код отличный от 200");
    }

    @Test
    void shouldEndPointSubtaskCreate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        postTask(json, url);
        subtask1.setParentKey(1);

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(subtask1);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        url = URI.create("http://localhost:8080/tasks/subtask/?taskKey=2");
        json = getTask(url).body();
        SubTask subtask = gson.fromJson(json, SubTask.class);
        assertEquals(subtask.getTaskDescription(), subtask1.getTaskDescription(), "Задача не соответствует ожидаемой.");

        url = URI.create("http://localhost:8080/tasks/subtask/epic/?taskKey=1");
        json = getTask(url).body();
        ArrayList<SubTask> tasks = gson.fromJson(json, new TypeToken<ArrayList<SubTask>>() {

        }.getType());
        assertEquals(tasks.size(), 1, "Количество подзадач эпика не соответствует ожидаемому.");

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = getTask(url).body();
        tasks = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {

        }.getType());
        assertEquals(tasks.size(), 1, "Количество подзадач эпика не соответствует ожидаемому.");

        url = URI.create("http://localhost:8080/tasks/subtask/?taskKey=2");
        assertEquals(removeTask(url).statusCode(), 200, "Вернулся код отличный от 200");
    }

    @Test
    void shouldEndPointHistoryCreate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        json = gson.toJson(task2);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        url = URI.create("http://localhost:8080/tasks/task/?taskKey=1");
        json = getTask(url).body();
        Task tempTask1 = gson.fromJson(json, Task.class);
        assertEquals(tempTask1.getTaskDescription(), task1.getTaskDescription(), "Задача не соответствует ожидаемой.");

        url = URI.create("http://localhost:8080/tasks/task/?taskKey=2");
        json = getTask(url).body();
        Task tempTask2 = gson.fromJson(json, Task.class);
        assertEquals(tempTask2.getTaskDescription(), task2.getTaskDescription(), "Задача не соответствует ожидаемой.");

        url = URI.create("http://localhost:8080/tasks/history/");
        json = getTask(url).body();
        ArrayList<Task> tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertEquals(tasks.size(), 2, "Размер истории не соответствует ожидаемому.");
    }

    @Test
    void shouldEndPointTasksCreate() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        json = gson.toJson(task2);
        assertEquals(postTask(json, url).statusCode(), 200, "Вернулся код отличный от 200");

        url = URI.create("http://localhost:8080/tasks/");
        json = getTask(url).body();
        ArrayList<Task> tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertEquals(tasks.size(), 2, "Размер списка задач не соответствует ожидаемому.");
    }

    HttpResponse<String> postTask(String json, URI url) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> getTask(URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> removeTask(URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
        taskServer.stop();
    }
}