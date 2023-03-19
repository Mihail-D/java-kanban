package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientPutExceptionTest {

    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        KVTaskClientPutException exception = new KVTaskClientPutException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}