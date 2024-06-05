package roomescape.client.payment;

import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;

public interface PaymentClient {

    PaymentConfirmationFromTossDto sendPaymentConfirm(PaymentConfirmationToTossDto paymentConfirmationToTossDto);
}
