package roomescape.payment.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.PaymentException;

@Component
public class PaymentClientResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().is5xxServerError() ||
                httpResponse.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getBody()));
        String responseBody = reader.lines().collect(Collectors.joining(""));
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String errorMessage = jsonNode.path("message").asText("Unknown error");
        throw new PaymentException(httpResponse.getStatusCode(), errorMessage);
    }
}
