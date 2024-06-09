package roomescape.service.client.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.controller.dto.response.PaymentErrorMessageResponse;
import roomescape.global.exception.RoomescapeException;

public class TossPaymentErrorHandler implements ErrorHandler {
    @Override
    public void handle(HttpRequest __, ClientHttpResponse response) throws IOException {
        byte[] responseBody = response.getBody().readAllBytes();
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBodyAsString = new String(responseBody, StandardCharsets.UTF_8);
        PaymentErrorMessageResponse result = objectMapper.readValue(responseBodyAsString, PaymentErrorMessageResponse.class);
        throw new RoomescapeException(result.message());
    }
}
