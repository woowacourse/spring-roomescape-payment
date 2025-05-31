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

@Component
@Slf4j
public class PaymentErrorHandler implements ResponseErrorHandler {

    private static final String SERVER_ERROR_MESSAGE = "서버의 문제가 발생했습니다.";

    private final ObjectMapper objectMapper;

    public PaymentErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        try {
            String responseBody = new String(response.getBody().readAllBytes());
            log.error("Payment API 에러 발생 - URL: {}, Method: {}, Status: {}, Response: {}",
                    url, method, response.getStatusCode(), responseBody);

            Response errorResponse = objectMapper.readValue(responseBody, Response.class);
            throw new PaymentClientException(errorResponse.message());
        } catch (JsonProcessingException e) {
            log.error("Payment API Json 에러 - URL: {}, Method: {}, Status: {}",
                    url, method, response.getStatusCode(), e);
            throw new PaymentClientException(SERVER_ERROR_MESSAGE);
        } catch (IOException e) {
            log.error("Payment API 처리 중 예상치 못한 에러 발생 - URL: {}, Method: {}, Status: {}",
                    url, method, response.getStatusCode(), e);
            throw new PaymentClientException(SERVER_ERROR_MESSAGE);
        }
    }

    private record Response(
            String code,
            String message
    ) {
    }
}
