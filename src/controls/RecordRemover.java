package controls;

import tasks.Epic;

import java.util.HashMap;

import static controls.ControlManager.epicStorage;

public class RecordRemover {
    RecordUpdater recordUpdater = new RecordUpdater();

    public void taskRemove(String key) {
        ControlManager.tasksStorage.remove(key);
    }

    public void epicRemove(String key) {
        Epic epic = epicStorage.get(key);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            ControlManager.subTasksStorage.remove(i);
        }

        epicStorage.remove(key);
    }

    public void subTaskRemove(String key, String parentKey) {
        Epic epic = epicStorage.get(parentKey);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;
        relatedSubTasks.remove(key);
        ControlManager.subTasksStorage.remove(key);
        recordUpdater.setEpicStatus(parentKey);
    }

    public void taskRemoveAll() {
        ControlManager.tasksStorage.clear();
    }

    public void epicsRemoveAll() {
        epicStorage.clear();
        ControlManager.subTasksStorage.clear();
    }

    public void subTasksRemoveAll() {
        for (String i : ControlManager.epicStorage.keySet()) {
            epicStorage.get(i).relatedSubTask.clear();
            epicStorage.get(i).setRecordStatus("NEW");
        }
        ControlManager.subTasksStorage.clear();
    }
}

