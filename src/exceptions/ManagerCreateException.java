package exceptions;

public class ManagerCreateException extends RuntimeException {
    public ManagerCreateException(String message) {
        System.out.println(message);
    }
}
