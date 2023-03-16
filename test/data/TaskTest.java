package data;

import tasks.TaskStatus;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    public final String taskName = "test_name";
    public final String taskDescription = "test_description";
    public final TaskStatus taskStatus = TaskStatus.NEW;
    public final Instant taskStartTime = Instant.now();
    public final Duration taskDuration = Duration.ofMinutes(15);
    public final Instant taskEndTime = taskStartTime.plus(taskDuration);
    private Task testTask;

    @BeforeEach
    void shouldCreateNewTask() {
        this.testTask = new Task(
                taskName,
                taskDescription,
                taskStatus,
                taskStartTime,
                taskDuration
        );
    }

    @Test
    void shouldChangeTaskStatus() {
        testTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, testTask.getTaskStatus(), "Статус не правильно изменился");
    }

    @Test
    void shouldSetProperEndTimeWhenTaskIsDone() {
        testTask.setTaskStatus(TaskStatus.DONE);
        testTask.setTaskEndTime(taskEndTime);
        assertEquals(taskEndTime, testTask.getTaskEndTime(), "Время завершения такси посчиталось не правильно");
    }

    @Test
    void shouldCreteTaskWithProperValues() {
        assertEquals(taskName, testTask.getTaskTitle(), "Задача создалась с верным названием");
        assertEquals(taskDescription, testTask.getTaskDescription(), "Задача создалась с верным описанием");
        assertEquals(taskStatus, testTask.getTaskStatus(), "Задача создалась с верным статусом");
        assertEquals(taskStartTime, testTask.getTaskStartTime(), "Задача создалась с указанным временем начала");
    }

    @Test
    void shouldCreateTask() {
        assertNotNull(testTask, "Задача не найдена");
    }

    @Test
    void shouldSetTitleAndDescription() {
        testTask.setTaskTitle("new_test_name");
        testTask.setTaskDescription("new_test_description");

        assertEquals("new_test_name", testTask.getTaskTitle(), "Не верно изменилось название");
        assertEquals("new_test_description", testTask.getTaskDescription(), "Не верно изменилось описание");
    }
}