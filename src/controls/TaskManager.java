package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void addEpic(Epic epic);

    void addTask(Task task);

    void addSubtask(SubTask subTask);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(SubTask subTask);

    void removeEpic(int taskKey);

    void removeTask(int taskKey);

    void removeSubtask(int taskKey);

    Epic getEpic(int taskKey);

    Task getTask(int taskKey);

    SubTask getSubtask(int taskKey);

    List<Epic> getEpicsCollection();

    List<SubTask> getSubtasksCollection();

    List<Task> getTasksCollection();

    List<SubTask> getEpicRelatedSubtasks(int epicTaskKey);

    void clearEpics();

    void clearTasks();

    void clearSubTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void load();
}
