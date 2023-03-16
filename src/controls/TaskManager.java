package controls;

import tasks.Task;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void addTask(Task task) throws IOException;

    void addEpic(Epic epicTask) throws IOException;

    void addSubTask(SubTask subTask, Integer epicTaskKey) throws IOException;

    void updateTask(Task task) throws IOException;

    void updateEpic(Epic epicTask, Integer taskKey) throws IOException;

    void updateSubTask(SubTask subTask, Integer taskKey) throws IOException;

    Task getTask(Integer taskKey);

    Epic getEpic(Integer taskKey);

    SubTask getSubTask(Integer taskKey);

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubTaskList();

    List<Task> collectAllTasks();

    boolean deleteTask(Integer taskKey) throws IOException;

    boolean deleteEpic(Integer taskKey) throws IOException;

    boolean deleteSubTask(int taskKey);

    boolean clearRelatedSubTusks(Integer taskKey) throws IOException;

    void setEpicStatus(Integer taskKey);

    List<Task> getPrioritizedTasks();

    void tasksClear();

    void load();

    void save();

    HashMap<Integer, Task> getTasksMap();

    HashMap<Integer, Epic> getEpicsMap();

    HashMap<Integer, SubTask> getSubTasksMap();

    List<String> getAllTasksStringList();
}