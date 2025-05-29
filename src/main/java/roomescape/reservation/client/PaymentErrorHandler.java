package roomescape.reservation.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.PaymentClientException;

@Component
public class PaymentErrorHandler implements ResponseErrorHandler {

    /**
     * TODO
     * 비타: TEST 400대 에러만 잘 잡는지
     * 리원: 리스폰스에서 message만 잘 가져오는가`
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes());
        if (responseBody.isBlank()) {
            throw new PaymentClientException();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Response errorResponse = mapper.readValue(responseBody, Response.class);
            throw new PaymentClientException(errorResponse.message());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Json 파싱 에러: " + responseBody, e);
        }
    }

    private record Response(
            String code,
            String message
    ) {
    }
}
