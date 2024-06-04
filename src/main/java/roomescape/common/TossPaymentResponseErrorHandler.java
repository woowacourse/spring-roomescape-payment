package roomescape.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.ClientException;

import java.io.IOException;

public class TossPaymentResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentResponseErrorHandler() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
               || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentClientErrorResponse paymentClientErrorResponse =
                objectMapper.readValue(response.getBody(), PaymentClientErrorResponse.class);

        if (TossErrorCodeNotForUser.hasContain(paymentClientErrorResponse.code())) {
            throw new ClientException("서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요. 문제가 계속될 경우 고객 지원팀에 연락해주세요.");
        }

        throw new ClientException(paymentClientErrorResponse.message());
    }
}
