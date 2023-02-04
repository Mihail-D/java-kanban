package exceptions;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(String message) {
        System.out.println(message);
    }
}
