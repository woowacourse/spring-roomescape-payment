package roomescape.payment.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.payment.controller.PaymentController;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = PaymentController.class)
public class PaymentHandler extends ResponseEntityExceptionHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleBadRequestException(final HttpClientErrorException e, final WebRequest request)
    {
        final Optional<TossErrorResponse> response = extractResponse(e.getResponseBodyAsString());
        if (response.isPresent()) {
            TossErrorResponse tossError = response.get();
            if (tossError.isPaymentError()) {
                log.error(tossError.message(), e.getStatusCode());
                return buildResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, "결제 관련 내부 오류가 발생했습니다.", request);
            }
            return buildResponseEntity(e, e.getStatusCode(), tossError.message(), request);
        }
        return buildResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, "결제 관련 내부 오류가 발생했습니다.", request);
    }

    private Optional<TossErrorResponse> extractResponse(final String rawBody) {
        try {
            return Optional.of(OBJECT_MAPPER.readValue(rawBody, TossErrorResponse.class));
        } catch (JsonProcessingException ex) {
            log.warn("JSON 파싱 실패: 원본 바디 반환", ex);
        }
        return Optional.empty();
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
