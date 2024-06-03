package roomescape.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.client.PaymentClient;
import roomescape.service.dto.PaymentRequestDto;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void pay(String orderId, long amount, String paymentKey) {
        try {
            PaymentRequestDto requestDto = new PaymentRequestDto(orderId, amount, paymentKey);
            paymentClient.requestPayment(requestDto);
        } catch (HttpClientErrorException e) {
            try {
                byte[] responseBody = e.getResponseBodyAsByteArray();
                ObjectMapper objectMapper = new ObjectMapper();
                String responseBodyAsString = new String(responseBody, StandardCharsets.UTF_8);
                PaymentErrorMessageResponse response = objectMapper.readValue(responseBodyAsString, PaymentErrorMessageResponse.class);
                throw new RoomescapeException(response.message());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
