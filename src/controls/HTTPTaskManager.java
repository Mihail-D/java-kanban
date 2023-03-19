package controls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.KVTaskClientLoadException;
import server.KVTaskClient;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient taskClient;
    private final Gson json;

    public HTTPTaskManager(String url) {
        super();
        try {
            this.taskClient = new KVTaskClient(url);
        } catch (Exception e) {
            throw new KVTaskClientLoadException("Клиент не загружен.");
        }
        json = getTaskGson();
        load();
    }

    @Override
    public void save() {
        taskClient.put("tasks", json.toJson(tasks.values()));
        taskClient.put("epics", json.toJson(epics.values()));
        taskClient.put("subtasks", json.toJson(subtasks.values()));
        taskClient.put("history", json.toJson(getHistory()));
    }

    @Override
    public void load() {
        ArrayList<Task> loadTasks = json.fromJson(
                taskClient.load("tasks"),
                new TypeToken<ArrayList<Task>>() {

                }.getType()
        );
        if (loadTasks != null) {
            for (Task task : loadTasks) {
                updateTask(task);
            }
        }
        ArrayList<Epic> loadEpics = json.fromJson(
                taskClient.load("epics"),
                new TypeToken<ArrayList<Epic>>() {

                }.getType()
        );

        if (loadEpics != null) {
            for (Epic epic : loadEpics) {
                updateEpic(epic);
            }
        }

        ArrayList<SubTask> loadSubtasks = json.fromJson(
                taskClient.load("subtasks"),
                new TypeToken<ArrayList<Task>>() {

                }.getType()
        );
        if (loadSubtasks != null) {
            for (SubTask subtask : loadSubtasks) {
                updateSubtask(subtask);
            }
        }
        ArrayList<Task> historyList = json.fromJson(
                taskClient.load("history"),
                new TypeToken<ArrayList<Task>>() {

                }.getType()
        );
        if (historyList != null) {
            for (Task task : historyList) {
                historyManager.add(task);
            }
        }
    }

    public static Gson getTaskGson() {
        return (new GsonBuilder().
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create());
    }
}
