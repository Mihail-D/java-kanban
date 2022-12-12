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
}


