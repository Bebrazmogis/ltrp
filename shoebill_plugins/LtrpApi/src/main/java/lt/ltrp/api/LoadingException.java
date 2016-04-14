package lt.ltrp.api;

/**
 * @author Bebras
 *         2016.02.10.
 */
public class LoadingException extends Exception {

    public LoadingException(String message) {
        super(message);
    }

    public LoadingException(Throwable cause) {
        super(cause);
    }

    public LoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
