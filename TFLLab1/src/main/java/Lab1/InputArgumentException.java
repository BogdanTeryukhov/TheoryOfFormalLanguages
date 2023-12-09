package Lab1;

public class InputArgumentException extends Exception{
    public InputArgumentException(){
        super();
    }
    public InputArgumentException(String message) {
        super(message);
    }
    public InputArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
    public InputArgumentException(Throwable cause) {
        super(cause);
    }
}
