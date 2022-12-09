package controls;

import records.Epic;
import records.SubTask;
import records.Task;

import java.util.HashMap;
import java.util.Scanner;

public class ControlManager {
    RecordCreator recordCreator = new RecordCreator();

    HashMap<String, Task> tasksStorage = new HashMap<>();
    HashMap<String, Epic> epicStorage = new HashMap<>();
    HashMap<String, SubTask> subTasksStorage = new HashMap<>();

    public void getControlOptions() {
        int item;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println("Создание. Сам объект должен передаваться в качестве параметра.");
                    System.out.println("Выбрать типа задачи");
                    item = scanner.nextInt();
                    switch (item) {
                        case 1:
                            Task task = recordCreator.taskCreate();
                            tasksStorage.put(task.getRecordId(), task);

                            /*for (String i : tasksStorage.keySet()) { // TODO TODO TODO
                                System.out.println(tasksStorage.get(i));
                            }*/

                            break;
                        case 2:
                            Epic epic = recordCreator.epicCreate();
                            epicStorage.put(epic.getRecordId(), epic);

                            /*for (String i : epicStorage.keySet()) { // TODO TODO TODO
                                System.out.println(epicStorage.get(i));
                            }*/

                            break;
                        case 3:
                            SubTask subTask = recordCreator.subTaskCreate();
                            subTasksStorage.put(subTask.getSubTaskId(), subTask);

                            for (String i : subTasksStorage.keySet()) { // TODO TODO TODO
                                System.out.println(subTasksStorage.get(i));
                            }

                            break;

                    }
                    break;
                /*case 2:
                    System.out.println("Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
                    break;
                case 3:
                    System.out.println("Получение по идентификатору.");
                    break;
                case 4:
                    System.out.println("Получение списка всех задач.");
                    break;
                case 5:
                    System.out.println("Удаление по идентификатору.");
                    break;
                case 6:
                    System.out.println("Удаление всех задач.");
                    break;
                case 7:
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


