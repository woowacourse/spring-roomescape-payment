package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.PaymentClientException;
import roomescape.exception.PaymentServerException;

@Component
@RequiredArgsConstructor
public class PaymentApiResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        boolean isServerError = ServerErrorCode.isServerError(errorResponse.code());
        if (isServerError) {
            throw new PaymentServerException(errorResponse.message());
        }
        throw new PaymentClientException(errorResponse.message());
    }

    enum ServerErrorCode {
        INVALID_REQUEST,
        INVALID_API_KEY,
        NOT_FOUND_TERMINAL_ID,
        INVALID_AUTHORIZE_AUTH,
        INVALID_UNREGISTERED_SUBMALL,
        NOT_REGISTERED_BUSINESS,
        UNAPPROVED_ORDER_ID,
        UNAUTHORIZED_KEY,
        REJECT_CARD_COMPANY,
        FORBIDDEN_REQUEST,
        INCORRECT_BASIC_AUTH_FORMAT,
        NOT_FOUND_PAYMENT,
        NOT_FOUND_PAYMENT_SESSION,
        FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
        FAILED_INTERNAL_SYSTEM_PROCESSING,
        UNKNOWN_PAYMENT_ERROR,
        ;

        public static boolean isServerError(String errorCode) {
            return Arrays.stream(ServerErrorCode.values())
                    .anyMatch(e -> e.name().equals(errorCode.toUpperCase()));
        }
    }
}
