package server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {

    public static final String SERVER_URI = "http://localhost:8078";
    public static final int KV_SERVER_PORT = 8078;
    public static final int PORT = 8080;
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String KEY = "ID";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String TASK_KEY = "TASKS";
    public static final String TASKS = "/tasks/";
    public static final String TASKS_TASK = "/tasks/task/";
    public static final String TASKS_EPIC = "/tasks/epic/";
    public static final String TASKS_SUBTASK = "/tasks/subTask/";
    public static final String TASKS_HISTORY = "/tasks/history/";
    public static final String HISTORY_KEY = "HISTORY";
}
