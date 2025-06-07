package roomescape.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.infrastructure.dto.response.TossPaymentErrorResponse;

public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentErrorHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        TossPaymentErrorResponse error = objectMapper.readValue(responseBody, TossPaymentErrorResponse.class);
        if (TossServerErrorCode.isServerError(error.code())) {
            throw new PaymentServerException("결제 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
        throw new PaymentClientException(error.message());
    }
}
