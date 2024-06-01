package roomescape.service.conponent;

import org.springframework.stereotype.Component;

import roomescape.service.dto.PaymentRequestDto;

@Component
public interface PaymentClient {

    void requestPayment(PaymentRequestDto body);
}
