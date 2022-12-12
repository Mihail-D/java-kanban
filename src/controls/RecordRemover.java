package controls;

public class RecordRemover {
    //ControlManager controlManager = new ControlManager();

    public void taskRemove(String key) {
        ControlManager.tasksStorage.remove(key);
    }


}

/*
* Удаление эпика : удаление всех его подзадач

   Удаление подзадачи :
   - обновление поля подзадач эпика
   - обновление его статуса

* */