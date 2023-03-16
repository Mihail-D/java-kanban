package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTaskTimeOverlappingExceptionTest {

    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        ManagerTaskTimeOverlappingException exception = new ManagerTaskTimeOverlappingException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}