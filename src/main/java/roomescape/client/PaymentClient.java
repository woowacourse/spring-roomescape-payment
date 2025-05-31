package roomescape.client;

import roomescape.dto.reservation.PaymentConfirmRequestDto;

public interface PaymentClient {

    void confirmPayment(PaymentConfirmRequestDto requestDto);
}
