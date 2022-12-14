package controls;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;
import java.util.Scanner;

public class ControlManager {

    static HashMap<String, Task> tasksStorage = new HashMap<>();
    static HashMap<String, Epic> epicStorage = new HashMap<>();
    static HashMap<String, SubTask> subTasksStorage = new HashMap<>();

    public void getControlOptions() {
        TaskCreator taskCreator = new TaskCreator();
        TaskUpdater taskUpdater = new TaskUpdater();
        TaskGetter taskGetter = new TaskGetter();
        TaskRemover taskRemover = new TaskRemover();
        Scanner scanner = new Scanner(System.in);
        int item;

        while (true) {
            //System.out.println("Тип действий с записями");
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    //System.out.println("Создание объекта. Сам объект должен передаваться в качестве параметра.");
                    //System.out.println("Выбрать типа объекта");
                    item = scanner.nextInt();
                    switch (item) {
                        case 1:
                            Task task = taskCreator.taskCreate();
                            tasksStorage.put(task.getTaskId(), task);
                            break;
                        case 2:
                            Epic epic = taskCreator.epicCreate();
                            epicStorage.put(epic.getTaskId(), epic);
                            break;
                        case 3:
                            SubTask subTask = taskCreator.subTaskCreate();
                            subTasksStorage.put(subTask.getSubTaskId(), subTask);
                            Epic parentTask = epicStorage.get(subTask.getTaskId());
                            parentTask.relatedSubTask.put(
                                    subTask.getSubTaskId(),
                                    String.valueOf(subTask.getTaskStatus())
                            );
                            break;
                    }
                    break;
                case 2:
                    //System.out.println("Обновление.");
                    //System.out.println("Какой тип записи обновить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            //System.out.println("Введите номер ключа");
                            String taskKey = scanner.next();
                            Task updateTask = taskUpdater.taskUpdate(taskKey);
                            tasksStorage.put(taskKey, updateTask);
                            break;
                        case 2:
                            //System.out.println("Введите ключ");
                            String epicKey = scanner.next();
                            Epic updateEpic = taskUpdater.epicUpdate(epicKey);
                            epicStorage.put(epicKey, updateEpic);
                            break;
                        case 3:
                            //System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            //System.out.println("Введите ключ основной задачи");
                            String parentKey = scanner.next();
                            taskUpdater.subTaskUpdate(subTaskKey, parentKey);
                            taskUpdater.setEpicStatus(parentKey);
                            break;
                    }
                    break;
                case 3:
                    //System.out.println("Получение по идентификатору.");
                    //System.out.println("Какой тип записи получить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            //System.out.println("Получение данных Задачи.");
                            //System.out.println("Введите ключ");
                            String taskKey = scanner.next();
                            taskGetter.getTaskTask(taskKey);
                            break;
                        case 2:
                            //System.out.println("Введите ключ");
                            String epicKey = scanner.next();
                            taskGetter.getEpicTask(epicKey);
                            break;
                        case 3:
                            //System.out.println("Получение данных Подзадачи.");
                            //System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            taskGetter.getSubTaskNote(subTaskKey);
                            break;
                    }
                    break;
                case 4:
                    //System.out.println("Получение списка всех задач.");
                    taskGetter.collectTasks();
                    taskGetter.collectEpics();
                    taskGetter.collectSubTasks();
                    break;
                case 5:
                    //System.out.println("Получение списка всех подзадач определённого эпика.");
                    //System.out.println("Введите ключ");
                    String key = scanner.next();
                    taskGetter.collectEpicSubtasks(key);
                    break;
                case 6:
                    //System.out.println("Удаление по идентификатору.");
                    //System.out.println("Введите ключ подзадачи");
                    String taskKey = scanner.next();
                    //System.out.println("Введите ключ эпика");
                    String parentKey = scanner.next();
                    taskRemover.taskRemove(taskKey);
                    taskRemover.epicRemove(taskKey);
                    taskRemover.subTaskRemove(taskKey, parentKey);
                    break;
                case 7:
                    //System.out.println("Удаление всех задач.");
                    taskRemover.taskRemoveAll();
                    taskRemover.epicsRemoveAll();
                    taskRemover.subTasksRemoveAll();
                    break;
                case 0:
                    return;
            }
        }
    }
}


