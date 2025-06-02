package roomescape.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class PaymentErrorResponse implements ErrorResponse {
    private final HttpStatusCode httpStatusCode;

    public PaymentErrorResponse(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatus(httpStatusCode.value());
    }

    public abstract String getMessage();
}
