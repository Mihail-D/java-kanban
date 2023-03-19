package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerLoadExceptionTest {
    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        ManagerLoadException exception = new ManagerLoadException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}