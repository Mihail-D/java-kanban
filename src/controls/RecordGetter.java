package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordGetter {

    public String getTaskRecord(String key) {
        Task task = ControlManager.tasksStorage.get(key);
        String taskTitle = task.getRecordTitle() + ",";
        String taskDescription = task.getRecordDescription() + ",";
        String taskKey = task.getRecordId() + ",";
        String taskStatus = task.getRecordStatus();

        return taskTitle + taskDescription + taskKey + taskStatus;
    }

    public String getEpicRecord(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        String epicTitle = epic.getRecordTitle() + ",";
        String epicDescription = epic.getRecordDescription() + ",";
        String epicKey = epic.getRecordId() + ",";
        String epicStatus = epic.getRecordStatus();

        return epicTitle + epicDescription + epicKey + epicStatus;
    }

    public String getSubTaskRecord(String key) {
        SubTask subTask = ControlManager.subTasksStorage.get(key);
        String epicTitle = subTask.getRecordTitle() + ",";
        String epicDescription = subTask.getRecordDescription() + ",";
        String epicKey = subTask.getRecordId() + ",";
        String epicStatus = subTask.getRecordStatus();

        return epicTitle + epicDescription + epicKey + epicStatus;
    }

    public ArrayList<String> collectTasks() {
        ArrayList<String> tasksList = new ArrayList<>();

        for (String i : ControlManager.tasksStorage.keySet()) {
            tasksList.add(getTaskRecord(i));
        }

        return tasksList;
    }

    public ArrayList<String> collectEpics() {
        ArrayList<String> epicsList = new ArrayList<>();

        for (String i : ControlManager.epicStorage.keySet()) {
            epicsList.add(getEpicRecord(i));
        }

        return epicsList;
    }

    public ArrayList<String> collectSubTasks() {
        ArrayList<String> subTasksList = new ArrayList<>();

        for (String i : ControlManager.subTasksStorage.keySet()) {
            subTasksList.add(getSubTaskRecord(i));
        }

        return subTasksList;
    }

    public ArrayList<String> collectEpicSubtasks(String key) {
        ArrayList<String> localTasksList = new ArrayList<>();
        Epic epic = ControlManager.epicStorage.get(key);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            localTasksList.add(getSubTaskRecord(i));
        }

        return localTasksList;
    }
}
