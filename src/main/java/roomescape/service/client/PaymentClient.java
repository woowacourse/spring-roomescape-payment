package roomescape.service.client;

import roomescape.service.dto.PaymentRequestDto;

public interface PaymentClient {

    void requestPayment(PaymentRequestDto body);
}
