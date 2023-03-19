package exceptions;

public class KVServerLoadException extends RuntimeException {
    public KVServerLoadException(String message) {
        super(message);
    }
}
