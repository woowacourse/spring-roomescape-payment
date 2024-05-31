package roomescape.domain.payment;

import static roomescape.domain.payment.PaymentApiErrorCode.UNKNOWN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentErrorParser {
    private final ObjectMapper objectMapper;

    public PaymentErrorParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PaymentApiError parse(String rawResponseBody) throws JsonProcessingException {
        PaymentApiError apiError;
        try {
            apiError = objectMapper.readValue(rawResponseBody, PaymentApiError.class);
        } catch (DatabindException e) {
            apiError = new PaymentApiError(UNKNOWN, "결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요.");
        }

        apiError = hideMessageIfNeed(apiError);
        return apiError;
    }

    private static PaymentApiError hideMessageIfNeed(PaymentApiError apiError) {
        if (apiError.isNeedToHide()) {
            apiError = new PaymentApiError(apiError.code(), "결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요.");
        }
        return apiError;
    }
}
