package roomescape.reservation.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.PaymentClientException;

@Slf4j
@Component
public class PaymentErrorHandler implements ResponseErrorHandler {
    private static final String ERROR_NOTIFICATION_MESSAGE = "문제가 발생했습니다. 다시 시도해주세요.";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes());
        if (responseBody.isBlank()) {
            log.error("응답 데이터가 비어 있습니다");
            throw new PaymentClientException(ERROR_NOTIFICATION_MESSAGE);
        }
        try {
            Response errorResponse = mapper.readValue(responseBody, Response.class);
            log.error("Toss Payments API 에러: {}", errorResponse.message());
            throw new PaymentClientException(ERROR_NOTIFICATION_MESSAGE);
        } catch (JsonProcessingException e) {
            log.error("Json 파싱 에러: {}", responseBody, e);
            throw new IllegalStateException(ERROR_NOTIFICATION_MESSAGE);
        }
    }

    private record Response(
            String code,
            String message
    ) {
    }
}
