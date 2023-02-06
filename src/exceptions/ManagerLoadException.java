package exceptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message) {
        System.out.println(message);
    }
}
