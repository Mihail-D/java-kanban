package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientLoadExceptionTest {

    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        KVTaskClientLoadException exception = new KVTaskClientLoadException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}