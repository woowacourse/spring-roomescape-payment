package roomescape.exception;

import org.springframework.http.HttpStatusCode;
import roomescape.dto.payment.CancelRequest;

public class PaymentCancelException extends RuntimeException {

    private final HttpStatusCode clientStatusCode;
    private final CancelRequest cancelRequest;
    private final Exception exception;

    public PaymentCancelException(final Exception exception,
                                  final HttpStatusCode clientStatusCode,
                                  final String errorMessage,
                                  final CancelRequest cancelRequest) {
        super(errorMessage);
        this.exception = exception;
        this.clientStatusCode = clientStatusCode;
        this.cancelRequest = cancelRequest;
    }

    public HttpStatusCode getClientStatusCode() {
        return clientStatusCode;
    }

    public CancelRequest getCancelRequest() {
        return cancelRequest;
    }

    public Exception getException() {
        return exception;
    }
}
