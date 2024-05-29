package roomescape.exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatusCode;

public class PaymentException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public PaymentException(HttpStatusCode statusCode, InputStream inputStream) throws IOException {
        super(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        this.statusCode = statusCode;
    }

    public PaymentException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
