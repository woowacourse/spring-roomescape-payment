package roomescape.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.payment.domain.Payment;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = Payment.class)
public class PaymentHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TossPaymentException.class)
    public ResponseEntity<Object> handleBadRequestException(final TossPaymentException e, final WebRequest request)
    {
        log.error(e.getMessage(), e);

        if(e.isServerError()) {
            return buildResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR,"결제 중 서버 오류가 발생했습니다.", request);
        }
        return buildResponseEntity(e, e.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(PaymentTimeoutException.class)
    public ResponseEntity<Object> handleTimeout(final PaymentTimeoutException e, final WebRequest request) {
        log.error(e.getMessage(), e);
        return buildResponseEntity(e, HttpStatus.GATEWAY_TIMEOUT, e.getMessage(), request);
    }

    private ResponseEntity<Object> buildResponseEntity(
            final Exception e,
            final HttpStatusCode status,
            final String message,
            final WebRequest request
    ) {
        final String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        final ProblemDetail body = super.createProblemDetail(e, status, message, path, null, request);
        return super.handleExceptionInternal(e, body, new HttpHeaders(), status, request);
    }
}
