package roomescape.service.command;

import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.dto.reservation.PaymentConfirmDto;

@Service
public class PaymentCommandService {

    private final PaymentClient paymentClient;

    public PaymentCommandService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResponseDto confirmPayment(PaymentConfirmDto requestDto) {
        Payment payment = paymentClient.confirmPayment(requestDto);
        return new PaymentResponseDto(payment.getOrderId(), payment.getTotalAmount());
    }
}
