package roomescape.global.exception;

import lombok.Getter;
import org.springframework.web.client.RestClientException;

@Getter
public class ClientFailException extends RestClientException {

    private final int statusCode;

    public ClientFailException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public static final class PaymentClientFailException extends ClientFailException {

        public PaymentClientFailException(final String message, final int statusCode) {
            super(message, statusCode);
        }
    }

}
