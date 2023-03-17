package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controls.HistoryManager;
import controls.Managers;
import controls.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static server.Constants.*;

public class HttpTaskServer {

    private HttpServer httpServer;
    public TaskManager taskManager;
    public HistoryManager historyManager;
    private static HashMap<String, HttpHandler> handlersStorage;

    private static void writeResponse(
            HttpExchange exchange,
            String responseString,
            int responseCode
    ) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        }
        else {
            byte[] bytes = responseString.getBytes(CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public void startHttpTaskServer() {
        try {
            httpServer = HttpServer.create();
            taskManager = Managers.getDefault();
            historyManager = Managers.getDefaultHistory();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            addHttpHandlers();
            handlersStorage.forEach(
                    (key, value) -> httpServer.createContext(key, value)
            );
            httpServer.start();
            System.out.println("Запускаем HttpTaskServer на порту " + PORT);
            System.out.println("Путь: http://localhost:" + PORT + "/");
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void stopHttpTaskServer() {
        httpServer.stop(5);
        System.out.println("HttpTaskServer остановлен");
    }

    private void addHttpHandlers() {
        handlersStorage = new HashMap<>();
        handlersStorage.put(TASKS, new AllTasksHandler());
        handlersStorage.put(TASKS_TASK, new ActionsWithTasksHandler(Task.class));
        handlersStorage.put(TASKS_SUBTASK, new ActionsWithTasksHandler(SubTask.class));
        handlersStorage.put(TASKS_EPIC, new ActionsWithTasksHandler(Epic.class));
        handlersStorage.put(TASKS_HISTORY, new HistoryHandler());
    }

    class AllTasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Task> tasksCollection = taskManager.collectAllTasks();
            if (!tasksCollection.isEmpty()) {
                Gson gson = new Gson();
                writeResponse(
                        exchange,
                        gson.toJson(tasksCollection),
                        HttpURLConnection.HTTP_OK
                );
            }
            else {
                writeResponse(
                        exchange,
                        "Данных не обнаружено",
                        HttpURLConnection.HTTP_NOT_FOUND
                );
            }
        }
    }

    class ActionsWithTasksHandler implements HttpHandler {

        private final Class<? extends Task> taskClass;

        ActionsWithTasksHandler(Class<? extends Task> taskClass) {
            super();
            this.taskClass = taskClass;
        }

        @Override
        public void handle(HttpExchange exchange) {
            switch (exchange.getRequestMethod()) {
                case GET:
                    get(exchange);
                    break;
                case POST:
                    post(exchange);
                    break;
                case DELETE:
                    delete(exchange);
                    break;
            }
        }

        private Optional<Integer> getTaskKey(HttpExchange exchange) {
            String[] uri = exchange.getRequestURI().getPath().split("/");
            try {
                return Optional.of(Integer.parseInt(uri[uri.length - 1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private String getTaskType(HttpExchange exchange) {
            String[] uri = exchange.getRequestURI().getPath().split("/");
            return uri[2];
        }

        private Optional<Task> getTaskToWorkWith(HttpExchange exchange) {
            try {
                Optional<Integer> optionalTaskId = getTaskKey(exchange);
                String taskType = getTaskType(exchange);
                if (optionalTaskId.isEmpty()) {
                    writeResponse(
                            exchange,
                            "Ключ задачи неправильный.",
                            HttpURLConnection.HTTP_BAD_REQUEST
                    );
                }
                else {
                    int taskId = optionalTaskId.get();
                    switch (taskType) {
                        case "task":
                            return Optional.of(taskManager.getTask(taskId));
                        case "subTask":
                            return Optional.of(taskManager.getSubTask(taskId));
                        case "epic":
                            return Optional.of(taskManager.getEpic(taskId));
                    }
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
            return Optional.empty();
        }

        private void get(HttpExchange exchange) {
            Optional<Task> optionalTask = getTaskToWorkWith(exchange);
            try {
                if (optionalTask.isEmpty()) {
                    writeResponse(
                            exchange,
                            "Задача не обнаружена.",
                            HttpURLConnection.HTTP_BAD_REQUEST
                    );
                }
                else {
                    Gson gson = new Gson();
                    Task task = optionalTask.get();
                    writeResponse(
                            exchange,
                            gson.toJson(task),
                            HttpURLConnection.HTTP_OK
                    );
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }

        private void post(HttpExchange exchange) {
            String taskType = getTaskType(exchange);
            Gson gson = new Gson();
            try {
                InputStream in = exchange.getRequestBody();
                byte[] bytes = in.readAllBytes();
                String value = new String(bytes, CHARSET);
                switch (taskType) {
                    case "task":
                        Task task = gson.fromJson(
                                value,
                                taskClass
                        );
                        taskManager.addTask(task);
                        writeResponse(
                                exchange,
                                "Задача создана с ключом " + task.getTaskKey(),
                                HttpURLConnection.HTTP_OK
                        );
                        break;
                    case "subTask":
                        SubTask subTask = (SubTask) gson.fromJson(
                                value,
                                taskClass
                        );
                        taskManager.addSubTask(subTask, subTask.getParentKey());
                        writeResponse(
                                exchange,
                                "Задача создана с ключом " + subTask.getTaskKey(),
                                HttpURLConnection.HTTP_OK
                        );
                        break;
                    case "epic":
                        Epic epicTask = (Epic) gson.fromJson(
                                value,
                                taskClass
                        );
                        taskManager.addEpic(epicTask);
                        writeResponse(
                                exchange,
                                "Задача создана с ключом " + epicTask.getTaskKey(),
                                HttpURLConnection.HTTP_OK
                        );
                        break;
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }

        private void delete(HttpExchange exchange) {
            Optional<Integer> optionalTaskId = getTaskKey(exchange);
            String taskType = getTaskType(exchange);
            try {
                if (optionalTaskId.isEmpty()) {
                    writeResponse(
                            exchange,
                            "Задача не обнаружена.",
                            HttpURLConnection.HTTP_BAD_REQUEST
                    );
                }
                else {
                    int taskId = optionalTaskId.get();
                    switch (taskType) {
                        case "task":
                            taskManager.deleteTask(taskId);
                            break;
                        case "subTask":
                            taskManager.deleteSubTask(taskId);
                            break;
                        case "epic":
                            taskManager.deleteEpic(taskId);
                            break;
                    }
                    writeResponse(
                            exchange,
                            "Задача с ключом " + taskId + " была успешно удалена",
                            HttpURLConnection.HTTP_OK
                    );
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Gson gson = new Gson();
            writeResponse(exchange, gson.toJson(historyManager.getHistory()), HttpURLConnection.HTTP_OK);
        }
    }
}
