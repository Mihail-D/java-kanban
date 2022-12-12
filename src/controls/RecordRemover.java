package controls;

import records.Epic;

import java.util.HashMap;

public class RecordRemover {
    //ControlManager controlManager = new ControlManager();

    public void taskRemove(String key) {
        ControlManager.tasksStorage.remove(key);
    }

    public void epicRemove(String key) {
        Epic epic = ControlManager.epicStorage.get(key);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;

        for (String i : relatedSubTasks.keySet()) {
            ControlManager.subTasksStorage.remove(i);
        }

        ControlManager.epicStorage.remove(key);
    }

    public void subTaskRemove(String key, String parentKey) {
        Epic epic = ControlManager.epicStorage.get(parentKey);
        HashMap<String, String> relatedSubTasks = epic.relatedSubTask;
        relatedSubTasks.remove(key);
        ControlManager.subTasksStorage.remove(key);
    }
}

/*
   Удаление подзадачи :
   - обновление поля подзадач эпика
   - обновление его статуса

* */