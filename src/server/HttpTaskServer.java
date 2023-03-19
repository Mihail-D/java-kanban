package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.KVTaskClientLoadException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import controls.HTTPTaskManager;
import controls.Managers;
import controls.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private final int PORT = 8080;
    private final Gson json;
    private final TaskManager taskManager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        this.json = HTTPTaskManager.getTaskGson();
        this.taskManager = Managers.getDefault();
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/tasks", this::tasksCollectionHandler);
        server.createContext("/tasks/task", this::taskHandler);
        server.createContext("/tasks/epic", this::epicHandler);
        server.createContext("/tasks/subtask", this::subTaskHandler);
        server.createContext("/tasks/history", this::historyHandler);
        server.createContext("/tasks/subtask/epic", this::handlerEpicSubtasks);
    }

    private void tasksCollectionHandler(HttpExchange h) {
        try {
            System.out.println("\n/tasksCollectionHandler");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, json.toJson(new ArrayList<>(taskManager.getPrioritizedTasks())));
            }
            else {
                System.out.println("/tasks ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    private void historyHandler(HttpExchange h) {
        try {
            System.out.println("\n/historyHandler");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, json.toJson(new ArrayList<>(taskManager.getHistory())));
            }
            else {
                System.out.println("/tasks ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    private void taskHandler(HttpExchange h) {
        try {
            System.out.println("\n/taskHandler");
            String requestMethod = h.getRequestMethod();
            int taskKey = getTaskIdFromPath(h.getRequestURI().getQuery());
            switch (requestMethod) {
                case "GET":
                    if (taskKey > 0) {
                        sendText(h, json.toJson(taskManager.getTask(taskKey)));
                    }
                    else {
                        sendText(h, json.toJson(new ArrayList<>(taskManager.getTasksCollection())));
                    }
                    break;
                case "POST":
                    Task task = json.fromJson(readText(h), Task.class);
                    if (taskKey > 0) {
                        taskManager.updateTask(task);
                    }
                    else {
                        taskManager.addTask(task);
                    }
                    sendText(h, "");
                    break;
                case "DELETE":
                    if (taskKey > 0) {
                        taskManager.removeTask(taskKey);
                    }
                    else {
                        taskManager.clearTasks();
                    }
                    sendText(h, "");
                    break;
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    private void epicHandler(HttpExchange h) {
        try {
            System.out.println("\n/epicHandler");
            String requestMethod = h.getRequestMethod();
            int taskKey = getTaskIdFromPath(h.getRequestURI().getQuery());
            switch (requestMethod) {
                case "GET":
                    if (taskKey > 0) {
                        sendText(h, json.toJson(taskManager.getEpic(taskKey)));
                    }
                    else {
                        sendText(h, json.toJson(new ArrayList<>(taskManager.getEpicsCollection())));
                    }
                    break;
                case "POST":
                    Epic epic = json.fromJson(readText(h), Epic.class);
                    if (taskKey > 0) {
                        taskManager.updateEpic(epic);
                    }
                    else {
                        taskManager.addEpic(epic);
                    }
                    sendText(h, "");
                    break;
                case "DELETE":
                    if (taskKey > 0) {
                        taskManager.removeEpic(taskKey);
                    }
                    else {
                        taskManager.clearEpics();
                    }
                    sendText(h, "");
                    break;
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    private void subTaskHandler(HttpExchange h) {
        try {
            System.out.println("\n/subTaskHandler");
            String requestMethod = h.getRequestMethod();
            int taskKey = getTaskIdFromPath(h.getRequestURI().getQuery());
            switch (requestMethod) {
                case "GET":
                    if (taskKey > 0) {
                        sendText(h, json.toJson(taskManager.getSubtask(taskKey)));
                    }
                    else {
                        sendText(h, json.toJson(new ArrayList<>(taskManager.getSubtasksCollection())));
                    }
                    break;
                case "POST":
                    SubTask subtask = json.fromJson(readText(h), SubTask.class);
                    if (taskKey > 0) {
                        taskManager.updateSubtask(subtask);
                    }
                    else {
                        taskManager.addSubtask(subtask);
                    }
                    sendText(h, "");
                    break;
                case "DELETE":
                    if (taskKey > 0) {
                        taskManager.removeSubtask(taskKey);
                    }
                    else {
                        taskManager.clearSubTasks();
                    }
                    sendText(h, "");
                    break;
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    private void handlerEpicSubtasks(HttpExchange h) {
        try {
            System.out.println("\n/handlerEpicSubtasks");
            String requestMethod = h.getRequestMethod();
            int taskKey = getTaskIdFromPath(h.getRequestURI().getQuery());
            if (requestMethod.equals("GET")) {
                if (taskKey > 0) {
                    sendText(h, json.toJson(new ArrayList<>(taskManager.getEpicRelatedSubtasks(taskKey))));
                }
                else {
                    sendText(h, "");
                }
            }
        } catch (IOException e) {
            throw new KVTaskClientLoadException("Ошибка при обработке запроса.");
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем TaskManager. Порт " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("TaskManager остановлен." + PORT);
        server.stop(0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private int getTaskIdFromPath(String query) {
        if (query == null) {
            return 0;
        }
        String[] paths = query.split("taskKey=");
        System.out.println(paths.length);
        if (paths.length > 1) {
            return Integer.parseInt(paths[1]);
        }
        return 0;
    }
}
