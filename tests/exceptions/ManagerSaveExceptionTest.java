package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerSaveExceptionTest {
    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        ManagerSaveException exception = new ManagerSaveException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}