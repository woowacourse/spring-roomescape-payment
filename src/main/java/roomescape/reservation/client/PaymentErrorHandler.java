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

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes());
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
