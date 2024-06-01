package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.conponent.PaymentClient;
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
            PaymentErrorMessageResponse response = e.getResponseBodyAs(PaymentErrorMessageResponse.class);
            throw new RoomescapeException(response.message());
        }
    }
}
