package controls;

import server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    public KVServer kvServer;

    @BeforeEach
    void StartUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager =  new HTTPTaskManager("http://localhost:8078/");
        initTasks();
    }

    @AfterEach
    public void terminateServer() {
        kvServer.stop();
    }

    @Test
    void shouldSaveAndLoad() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getTaskKey());
        taskManager.getTask(task2.getTaskKey());
        taskManager.getTask(task3.getTaskKey());

        HTTPTaskManager taskManager1 = new HTTPTaskManager("http://localhost:8078/");

        assertNotNull(taskManager1.getTasksCollection(), "Ошибка в сохранение/загрузке с сервера");
        assertEquals(3, taskManager1.getTasksCollection().size(), "Ошибка в сохранение/загрузке с сервера");
    }
}