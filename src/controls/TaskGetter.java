package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskGetter {

    public String getTaskTask(String key) {
        Task task = ControlManager.tasksStorage.get(key);
        String taskTitle = task.getTaskTitle() + ",";
        String taskDescription = task.getTaskDescription() + ",";
        String taskKey = task.getTaskId() + ",";
        String taskStatus = task.getTaskStatus();

        return taskTitle + taskDescription + taskKey + taskStatus;
    }

    public String getEpicTask(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        String epicTitle = epic.getTaskTitle() + ",";
        String epicDescription = epic.getTaskDescription() + ",";
        String epicKey = epic.getTaskId() + ",";
        String epicStatus = epic.getTaskStatus();

        return epicTitle + epicDescription + epicKey + epicStatus;
    }

    public String getSubTaskNote(String key) {
        SubTask subTask = ControlManager.subTasksStorage.get(key);
        String epicTitle = subTask.getTaskTitle() + ",";
        String epicDescription = subTask.getTaskDescription() + ",";
        String epicKey = subTask.getTaskId() + ",";
        String epicStatus = subTask.getTaskStatus();

        return epicTitle + epicDescription + epicKey + epicStatus;
    }

    public ArrayList<String> collectTasks() {
        ArrayList<String> tasksList = new ArrayList<>();

        for (String i : ControlManager.tasksStorage.keySet()) {
            tasksList.add(getTaskTask(i));
        }

        return tasksList;
    }

    public ArrayList<String> collectEpics() {
        ArrayList<String> epicsList = new ArrayList<>();

        for (String i : ControlManager.epicStorage.keySet()) {
            epicsList.add(getEpicTask(i));
        }

        return epicsList;
    }

    public ArrayList<String> collectSubTasks() {
        ArrayList<String> subTasksList = new ArrayList<>();

        for (String i : ControlManager.subTasksStorage.keySet()) {
            subTasksList.add(getSubTaskNote(i));
        }

        return subTasksList;
    }

    public ArrayList<String> collectEpicSubtasks(String key) {
        ArrayList<String> localTasksList = new ArrayList<>();
        Epic epic = ControlManager.epicStorage.get(key);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            localTasksList.add(getSubTaskNote(i));
        }

        return localTasksList;
    }
}
