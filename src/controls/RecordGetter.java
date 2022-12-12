package controls;

import records.Epic;
import records.SubTask;
import records.Task;

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
}
