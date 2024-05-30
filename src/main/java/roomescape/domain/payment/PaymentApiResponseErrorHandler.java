package roomescape.domain.payment;

import static roomescape.domain.payment.PaymentApiErrorCode.UNKNOWN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class PaymentApiResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    public PaymentApiResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String rawResponseBody = new String(bytes);
        PaymentApiError apiError = parse(rawResponseBody);
        throw new ApiCallException(apiError);
    }

    private PaymentApiError parse(String rawResponseBody) throws JsonProcessingException {
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
