package data;

import controls.InMemoryTaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest extends TaskTest {

    private static InMemoryTaskManager manager;
    private SubTask testSubTask1;
    private SubTask testSubTask2;
    private SubTask testSubTask3;
    private Epic testEpicTask;

    @BeforeEach
    void addEpic() throws IOException {
        manager = new InMemoryTaskManager();
        Task.setIdCounter(0);
        this.testSubTask1 = new SubTask(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 14),
                taskDuration
        );
        this.testSubTask2 = new SubTask(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 16),
                taskDuration
        );
        this.testSubTask3 = new SubTask(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 17),
                taskDuration
        );
        this.testEpicTask = new Epic(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 19),
                taskDuration
        );
        manager.addEpic(testEpicTask);
        manager.addSubTask(testSubTask1, testEpicTask.getTaskKey());
        manager.addSubTask(testSubTask2, testEpicTask.getTaskKey());
        manager.addSubTask(testSubTask3, testEpicTask.getTaskKey());
    }

    @Test
    void shouldAddNewSubTaskIdToEpicTask() throws IOException {
        Set<SubTask> set = new HashSet<>();
        set.add(testSubTask1);
        set.add(testSubTask2);
        set.add(testSubTask3);
        assertEquals(set, testEpicTask.getRelatedSubTasks());

        SubTask testSubTask4 = new SubTask(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime.plusSeconds(60 * 60 * 20),
                taskDuration
        );
        manager.addSubTask(testSubTask4, testEpicTask.getTaskKey());
        set.add(testSubTask4);
        assertEquals(set, testEpicTask.getRelatedSubTasks());
    }

    @Test
    void shouldRemoveSubTask() {
        Set<SubTask> set = new HashSet<>();
        set.add(testSubTask1);
        set.add(testSubTask2);
        set.add(testSubTask3);
        assertEquals(set, testEpicTask.getRelatedSubTasks());
        testEpicTask.removeChildSubTask(testSubTask1);
        set.remove(testSubTask1);
        assertEquals(set, testEpicTask.getRelatedSubTasks());

    }

    @Test
    void shouldGetSubTaskIds() {
        assertNotNull(testEpicTask);
        assertNotNull(testSubTask1);
        assertNotNull(testSubTask2);
        assertNotNull(testSubTask3);

        Set<SubTask> set = new HashSet<>();
        set.add(testSubTask1);
        set.add(testSubTask2);
        set.add(testSubTask3);
        assertEquals(set, testEpicTask.getRelatedSubTasks());
    }
}