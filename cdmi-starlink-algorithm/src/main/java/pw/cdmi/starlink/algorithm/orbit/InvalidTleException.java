package pw.cdmi.starlink.algorithm.orbit;

public class InvalidTleException extends Exception {
    private static final long serialVersionUID = -1993764584699304542L;

    public InvalidTleException() {
    }

    public InvalidTleException(String message) {
        super(message);
    }

    public InvalidTleException(Throwable cause) {
        super(cause);
    }

    public InvalidTleException(String message, Throwable cause) {
        super(message, cause);
    }
}
