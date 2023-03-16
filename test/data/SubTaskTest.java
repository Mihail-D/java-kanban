package data;

import controls.InMemoryTaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest extends TaskTest {

    private SubTask testSubTask;
    private Epic testEpicTask;

    @BeforeEach
    void shouldCreateSubTask() throws IOException {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task.setIdCounter(0);
        this.testSubTask = new SubTask(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 11),
                taskDuration
        );
        this.testEpicTask = new Epic(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 10),
                taskDuration
        );
        manager.addEpic(testEpicTask);
        manager.addSubTask(testSubTask, testEpicTask.getTaskKey());
    }

    @Test
    void shouldGetEpicIdOfSubTask() {
        testSubTask.setRelatedSubtasks(11);
        assertEquals(11, testSubTask.getParentKey(), "Не верно выдает epicTaskKey в методе GetEpicIdOfSubTask");
    }

    @Test
    void shouldSetEpicIdOfSubTask() {
        assertEquals(testEpicTask.getTaskKey(), testSubTask.getParentKey(), "Не верный epicTaskKey при создании");
        testSubTask.setRelatedSubtasks(11);
        assertEquals(11, testSubTask.getParentKey(), "Не верный epicTaskKey при изменении");
    }
}