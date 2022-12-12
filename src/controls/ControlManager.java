package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;

public class ControlManager {

    static HashMap<String, Task> tasksStorage = new HashMap<>();
    static HashMap<String, Epic> epicStorage = new HashMap<>();
    static HashMap<String, SubTask> subTasksStorage = new HashMap<>();

    public void getControlOptions() {
        RecordCreator recordCreator = new RecordCreator();
        RecordUpdater recordUpdater = new RecordUpdater();
        RecordGetter recordGetter = new RecordGetter();
        RecordRemover recordRemover = new RecordRemover();
        int item;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Тип действий с записями");

            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("Создание объекта. Сам объект должен передаваться в качестве параметра.");
                    System.out.println("Выбрать типа объекта");
                    item = scanner.nextInt();
                    switch (item) {
                        case 1:
                            Task task = recordCreator.taskCreate();
                            tasksStorage.put(task.getRecordId(), task);
                            break;
                        case 2:
                            Epic epic = recordCreator.epicCreate();
                            epicStorage.put(epic.getRecordId(), epic);
                            break;
                        case 3:
                            SubTask subTask = recordCreator.subTaskCreate();
                            subTasksStorage.put(subTask.getSubTaskId(), subTask);
                            Epic parentTask = epicStorage.get(subTask.getRecordId());
                            parentTask.relatedSubTask.put(subTask.getSubTaskId(), subTask.getRecordStatus());
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Обновление.");
                    System.out.println("Какой тип записи обновить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Введите номер ключа");
                            String taskKey = scanner.next();
                            Task updateTask = recordUpdater.taskUpdate(taskKey);
                            tasksStorage.put(taskKey, updateTask);
                            break;
                        case 2:
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();
                            Epic updateEpic = recordUpdater.epicUpdate(epicKey);
                            epicStorage.put(epicKey, updateEpic);
                            break;
                        case 3:
                            System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            System.out.println("Введите ключ основной задачи");
                            String parentKey = scanner.next();
                            recordUpdater.subTaskUpdate(subTaskKey, parentKey);
                            recordUpdater.setEpicStatus(parentKey);
                            break;

                    }
                    break;
                case 3:
                    System.out.println("Получение по идентификатору.");
                    System.out.println("Какой тип записи получить?");
                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Получение данных Задачи.");
                            System.out.println("Введите ключ");
                            String taskKey = scanner.next();
                            recordGetter.getTaskRecord(taskKey);
                            break;
                        case 2:
                            System.out.println("Получение данных Эпика.");
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();
                            recordGetter.getEpicRecord(epicKey);
                            break;
                        case 3:
                            System.out.println("Получение данных Подзадачи.");
                            System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            recordGetter.getSubTaskRecord(subTaskKey);
                            break;
                    }
                    break;
                case 4:
                    System.out.println("Получение списка всех задач.");
                    recordGetter.collectTasks();
                    recordGetter.collectEpics();
                    recordGetter.collectSubTasks();
                    break;
                case 5:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
                    System.out.println("Введите ключ");
                    String key = scanner.next();
                    recordGetter.collectEpicSubtasks(key);
                    break;
                case 6:
                    System.out.println("Удаление по идентификатору.");
                    System.out.println("Введите ключ");
                    String taskKey = scanner.next();
                    recordRemover.taskRemove(taskKey);

                    System.out.println(tasksStorage); // TODO 



                    break;
/*                case 7:
                    System.out.println("Удаление всех задач.");
                    break;*/
                case 0:
                    return;
            }

        }
    }
    public void menuPrint() {
        System.out.println("1 - Создание. Сам объект должен передаваться в качестве параметра.");
        System.out.println("2 - Обновление. Новая версия объекта с верным id передаётся в виде параметра" + ".");
        System.out.println("3 - Получение по идентификатору.");
        System.out.println("4 - Получение списка всех задач.");
        System.out.println("5 - Удаление по идентификатору.");
        System.out.println("6 - Удаление всех задач.");
        System.out.println("7 - Получение списка всех подзадач определённого эпика.");
        System.out.println("0 - Выход.");
    }
}


