package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KVTaskClientRegisterExceptionTest {
    @Test
    void shouldCreateException() {
        String exceptionMessage = "exception message";
        KVTaskClientRegisterException exception = new KVTaskClientRegisterException(exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}