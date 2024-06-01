package roomescape.payment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.global.exception.PaymentException;
import roomescape.payment.dto.PaymentErrorResponse;

public class TossPaymentErrorHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void handle(HttpRequest req, ClientHttpResponse res) throws IOException {
        PaymentErrorResponse paymentErrorResponse = OBJECT_MAPPER.readValue(res.getBody(), PaymentErrorResponse.class);
        HandlingTargetErrorCodes handledType = HandlingTargetErrorCodes.from(paymentErrorResponse.code());
        throw new PaymentException(handledType.handledMessage, handledType.handledStatusCode);
    }

    private enum HandlingTargetErrorCodes {
        REJECT_ACCOUNT_PAYMENT(HttpStatus.BAD_REQUEST, "잔액 부족"),
        UNAUTHORIZED_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패"),
        DEFAULT(HttpStatus.BAD_REQUEST, "결제 실패");

        private final HttpStatus handledStatusCode;
        private final String handledMessage;

        HandlingTargetErrorCodes(HttpStatus handledStatusCode, String handledMessage) {
            this.handledStatusCode = handledStatusCode;
            this.handledMessage = handledMessage;
        }

        private static HandlingTargetErrorCodes from(String targetCode) {
            return Arrays.stream(values())
                    .filter(ht -> ht.name().equals(targetCode))
                    .findAny()
                    .orElse(DEFAULT);
        }
    }
}
