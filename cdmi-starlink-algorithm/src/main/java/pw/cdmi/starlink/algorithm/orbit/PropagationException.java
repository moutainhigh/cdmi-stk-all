package pw.cdmi.starlink.algorithm.orbit;

public class PropagationException extends RuntimeException{
    public PropagationException() {

    }
    public PropagationException(String message)  {
        super(message);
    }
    public PropagationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
