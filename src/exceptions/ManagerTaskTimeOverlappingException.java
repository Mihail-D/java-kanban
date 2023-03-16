package exceptions;

public class ManagerTaskTimeOverlappingException extends RuntimeException {
    public ManagerTaskTimeOverlappingException(String message) {
        super(message);
    }
}