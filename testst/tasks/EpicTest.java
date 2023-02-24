package tasks;

import controls.InMemoryTaskManager;
import controls.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskStages.*;
import static tasks.TaskTypes.*;
import static tasks.TaskTypes.SUB_TASK;

class EpicTest {

    TaskManager taskManager;
    public Epic epic;
    public SubTask subtask1;
    public SubTask subtask2;
    public SubTask subtask3;
    public SubTask subtask4;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

        epic = new Epic("epicTask", "description", false, NEW, EPIC,
                LocalDateTime.MAX, Duration.ZERO, new LinkedHashMap<>()
        );

        subtask1 = new SubTask("subTask_1", "description", false, NEW,
                SUB_TASK, LocalDateTime.parse("23.02.2023_06:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        subtask2 = new SubTask("subTask_2", "description", false, NEW,
                SUB_TASK, LocalDateTime.parse("23.02.2023_08:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        subtask3 = new SubTask("subTask_3", "description", false, NEW,
                SUB_TASK, LocalDateTime.parse("23.02.2023_10:00", formatter), Duration.ofMinutes(60), "e.1"
        );
        subtask4 = new SubTask("subTask_4", "description", false, NEW,
                SUB_TASK, LocalDateTime.parse("23.02.2023_12:00", formatter), Duration.ofMinutes(60), "e.1"
        );
    }

    @Test
    public void shouldReturnEmptyList() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);
        assertEquals(4, epic.relatedSubTask.size());
        taskManager.taskDelete(subtask1.getTaskId());
        taskManager.taskDelete(subtask2.getTaskId());
        taskManager.taskDelete(subtask3.getTaskId());
        taskManager.taskDelete(subtask4.getTaskId());
        assertEquals(0, epic.relatedSubTask.size());
    }

    @Test
    public void shouldAllSubtasksAreNew() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);
        assertEquals(NEW, epic.getTaskStatus());
        assertEquals(NEW, subtask1.getTaskStatus());
        assertEquals(NEW, subtask2.getTaskStatus());
        assertEquals(NEW, subtask3.getTaskStatus());
        assertEquals(NEW, subtask4.getTaskStatus());
    }

    @Test
    public void shouldAllSubtasksAreDone() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);

        taskManager.subTaskUpdate("s.2", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.3", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_08:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.4", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_10:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.5", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_12:00", Duration.ofMinutes(59)
        );
        assertEquals(DONE, epic.getTaskStatus());
        assertEquals(DONE, subtask1.getTaskStatus());
        assertEquals(DONE, subtask2.getTaskStatus());
        assertEquals(DONE, subtask3.getTaskStatus());
        assertEquals(DONE, subtask4.getTaskStatus());
    }

    @Test
    public void shouldSubtasksAreNewDone() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);

        taskManager.subTaskUpdate("s.2", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.3", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_08:00", Duration.ofMinutes(59)
        );

        assertEquals(IN_PROGRESS, epic.getTaskStatus());
        assertEquals(DONE, subtask1.getTaskStatus());
        assertEquals(DONE, subtask2.getTaskStatus());
        assertEquals(NEW, subtask3.getTaskStatus());
        assertEquals(NEW, subtask4.getTaskStatus());
    }

    @Test
    public void shouldSubtasksAreInProgress() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);

        taskManager.subTaskUpdate("s.2", "newTitle_1", "newDescription_1",
                "IN_PROGRESS", "e.1", "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.3", "newTitle_1", "newDescription_1",
                "IN_PROGRESS", "e.1", "23.02.2023_08:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.4", "newTitle_1", "newDescription_1",
                "IN_PROGRESS", "e.1", "23.02.2023_10:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.5", "newTitle_1", "newDescription_1",
                "IN_PROGRESS", "e.1", "23.02.2023_12:00", Duration.ofMinutes(59)
        );

        assertEquals(IN_PROGRESS, epic.getTaskStatus());
        assertEquals(IN_PROGRESS, subtask1.getTaskStatus());
        assertEquals(IN_PROGRESS, subtask2.getTaskStatus());
        assertEquals(IN_PROGRESS, subtask3.getTaskStatus());
        assertEquals(IN_PROGRESS, subtask4.getTaskStatus());
    }

    @Test
    public void shouldSubtasksAreMix() {
        taskManager.epicAdd(epic);
        taskManager.subTaskAdd(subtask1);
        taskManager.subTaskAdd(subtask2);
        taskManager.subTaskAdd(subtask3);
        taskManager.subTaskAdd(subtask4);

        taskManager.subTaskUpdate("s.2", "newTitle_1", "newDescription_1",
                "NEW", "e.1", "23.02.2023_06:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.3", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_08:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.4", "newTitle_1", "newDescription_1",
                "IN_PROGRESS", "e.1", "23.02.2023_10:00", Duration.ofMinutes(59)
        );
        taskManager.subTaskUpdate("s.5", "newTitle_1", "newDescription_1",
                "DONE", "e.1", "23.02.2023_12:00", Duration.ofMinutes(59)
        );

        assertEquals(IN_PROGRESS, epic.getTaskStatus());
        assertEquals(NEW, subtask1.getTaskStatus());
        assertEquals(DONE, subtask2.getTaskStatus());
        assertEquals(IN_PROGRESS, subtask3.getTaskStatus());
        assertEquals(DONE, subtask4.getTaskStatus());
    }
}