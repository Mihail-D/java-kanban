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

                           /* for (String i : tasksStorage.keySet()) { // TODO TODO TODO
                                System.out.println(tasksStorage.get(i));
                            }*/

                            break;
                        case 2:
                            Epic epic = recordCreator.epicCreate();
                            epicStorage.put(epic.getRecordId(), epic);

                            for (String i : epicStorage.keySet()) { // TODO TODO TODO
                                System.out.println("From case 2 loop " + epicStorage.get(i));

                            }

                            break;
                        case 3:
                            SubTask subTask = recordCreator.subTaskCreate();
                            subTasksStorage.put(subTask.getSubTaskId(), subTask);
                            Epic parentTask = epicStorage.get(subTask.getRecordId());
                            parentTask.relatedSubTask.put(subTask.getSubTaskId(), subTask.getRecordStatus());
                            // TODO
                        /*    System.out.println("getSubTaskId " + subTask.getSubTaskId()); // sub.1
                            System.out.println("getRecordId: " + subTask.getRecordId()); // e.1
                            System.out.println(epicStorage.get(subTask.getRecordId()));*/

                            break;

                    }
                    break;
                case 2:
                    System.out.println("Обновление.");
                    System.out.println("Какой вид записи обновить?");

                    item = scanner.nextInt();

                    switch (item) {
                        case 1:
                            System.out.println("Введите номер ключа");
                            String taskKey = scanner.next();
                            Task updateTask = recordUpdater.taskUpdate(taskKey);
                            tasksStorage.put(taskKey, updateTask);

                            // TODO
                            System.out.println(tasksStorage.get("t.1"));

                            break;
                        case 2:
                            System.out.println("Введите ключ");
                            String epicKey = scanner.next();
                            Epic updateEpic = recordUpdater.epicUpdate(epicKey);
                            epicStorage.put(epicKey, updateEpic);

                            // TODO
                            System.out.println(epicStorage.get("e.1"));

                            break;
                        case 3:
                            System.out.println("Введите ключ");
                            String subTaskKey = scanner.next();
                            System.out.println("Введите ключ основной задачи");
                            String parentKey = scanner.next();

                            recordUpdater.subTaskUpdate(subTaskKey, parentKey);
                            recordUpdater.setEpicStatus(parentKey);

                            // TODO
                            System.out.println(epicStorage.get("e.1"));
                            System.out.println(subTasksStorage.get("sub.1"));

                            break;

                    }

                    break;
/*                case 3:
                    System.out.println("Получение по идентификатору.");
                    break;*/
/*                case 4:
                    System.out.println("Получение списка всех задач.");
                    break;*/
/*                case 5:
                    System.out.println("Удаление по идентификатору.");
                    break;*/
/*                case 6:
                    System.out.println("Удаление всех задач.");
                    break;*/
/*                case 7:
                    System.out.println("Получение списка всех подзадач определённого эпика.");
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

    public void fillStorages() {
        tasksStorage.put("t.1", new Task("Title1", "Describe1", "t.1", "NEW"));
        tasksStorage.put("t.2", new Task("Title2", "Describe2", "t.2", "NEW"));
        tasksStorage.put("t.3", new Task("Title3", "Describe3", "t.3", "NEW"));
        tasksStorage.put("t.4", new Task("Title4", "Describe4", "t.4", "NEW"));
        tasksStorage.put("t.5", new Task("Title5", "Describe5", "t.5", "NEW"));
        tasksStorage.put("t.6", new Task("Title6", "Describe6", "t.6", "NEW"));
        tasksStorage.put("t.7", new Task("Title7", "Describe7", "t.7", "NEW"));

        epicStorage.put("e.1", new Epic("Title1", "Describe1", "e.1", "NEW", new HashMap<>()));
        epicStorage.put("e.2", new Epic("Title2", "Describe2", "e.2", "NEW", new HashMap<>()));
        epicStorage.put("e.3", new Epic("Title3", "Describe3", "e.3", "NEW", new HashMap<>()));
        epicStorage.put("e.4", new Epic("Title4", "Describe4", "e.4", "NEW", new HashMap<>()));
        epicStorage.put("e.5", new Epic("Title5", "Describe5", "e.5", "NEW", new HashMap<>()));
        epicStorage.put("e.6", new Epic("Title6", "Describe6", "e.6", "NEW", new HashMap<>()));
        epicStorage.put("e.7", new Epic("Title7", "Describe7", "e.7", "NEW", new HashMap<>()));

        /*subTasksStorage.put("sub.1", new SubTask("Title1", "Describe1", "sub.1",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.2", new SubTask("Title2", "Describe2", "sub.2",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.3", new SubTask("Title3", "Describe3", "sub.3",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.4", new SubTask("Title4", "Describe4", "sub.4",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.5", new SubTask("Title5", "Describe5", "sub.5",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.6", new SubTask("Title6", "Describe6", "sub.6",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.7", new SubTask("Title7", "Describe7", "sub.7",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.8", new SubTask("Title8", "Describe8", "sub.8",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.9", new SubTask("Title9", "Describe9", "sub.9",
                "NEW", "e.4"
        ));
        subTasksStorage.put("sub.10", new SubTask("Title10", "Describe10", "sub.10",
                "NEW", "e.4"
        ));*/

        System.out.println(tasksStorage);
        System.out.println();
        System.out.println(epicStorage);
        System.out.println();
        System.out.println(subTasksStorage);

    }
}


