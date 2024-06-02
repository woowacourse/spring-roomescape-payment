package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public class PaymentException extends RuntimeException {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String code;

    private PaymentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static PaymentException from(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = OBJECT_MAPPER.readValue(response.getBody(), PaymentErrorResponse.class);

        return new PaymentException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }

    public String getCode() {
        return code;
    }
}
