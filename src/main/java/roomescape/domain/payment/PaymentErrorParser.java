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
            apiError = new PaymentApiError(UNKNOWN, "알 수 없는 에러 코드입니다. 문서를 확인해주세요!");
        }
        return apiError;
    }
}
