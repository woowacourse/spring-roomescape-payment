package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class PaymentException extends RuntimeException {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Set<String> SERVER_ERROR_CODES = Set.of(
            "INVALID_API_KEY",
            "NOT_FOUND_TERMINAL_ID",
            "INVALID_AUTHORIZE_AUTH",
            "INVALID_UNREGISTERED_SUBMALL",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "FORBIDDEN_REQUEST",
            "INCORRECT_BASIC_AUTH_FORMAT",
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "FAILED_INTERNAL_SYSTEM_PROCESSING",
            "UNKNOWN_PAYMENT_ERROR"
    );

    private final HttpStatus status;
    private final String code;

    private PaymentException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public static PaymentException from(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = OBJECT_MAPPER.readValue(response.getBody(), PaymentErrorResponse.class);

        if (SERVER_ERROR_CODES.contains(paymentErrorResponse.code())) {
            return new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, paymentErrorResponse.code(), "결제 서버에 문제가 발생했습니다.");
        }
        return new PaymentException(HttpStatus.BAD_REQUEST, paymentErrorResponse.code(), paymentErrorResponse.message());
    }

    public HttpStatus getStatus() {
        return status;
    }
}
