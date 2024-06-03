package roomescape.client.payment;

import roomescape.client.payment.dto.PaymentConfirmToTossDto;

public interface PaymentClient {

    void sendPaymentConfirm(PaymentConfirmToTossDto paymentConfirmToTossDto);

}
