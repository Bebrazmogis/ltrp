package lt.ltrp;

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
}
