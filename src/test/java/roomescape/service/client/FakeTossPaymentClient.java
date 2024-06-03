package roomescape.service.client;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.service.client.PaymentClient;
import roomescape.service.dto.PaymentRequestDto;

public class FakeTossPaymentClient implements PaymentClient {
    @Override
    public void requestPayment(PaymentRequestDto body) {
        if (body.orderId().isBlank() || body.paymentKey().isBlank()) {
            try {
                PaymentErrorMessageResponse request = new PaymentErrorMessageResponse("INVALID_AUTHORIZE_AUTH", "유효하지 않은 인증 방식입니다.");
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBody = objectMapper.writeValueAsString(request);
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", requestBody.getBytes(), StandardCharsets.UTF_8);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
