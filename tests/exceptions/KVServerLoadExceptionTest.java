package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KVServerLoadExceptionTest {

    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        KVServerLoadException exception = new KVServerLoadException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}