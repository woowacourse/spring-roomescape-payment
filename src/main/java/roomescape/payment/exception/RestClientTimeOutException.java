package roomescape.payment.exception;

public class RestClientTimeOutException extends RuntimeException {

    public RestClientTimeOutException(Exception exception) {
        super(exception);
    }
}
