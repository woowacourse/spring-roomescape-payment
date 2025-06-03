package roomescape.service.command;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.dto.reservation.PaymentConfirmDto;

@Service
public class PaymentCommandService {

    private final PaymentService paymentService;

    public PaymentCommandService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResponseDto confirmPayment(PaymentConfirmDto requestDto) {
        Payment payment = paymentService.confirmPayment(requestDto);
        return new PaymentResponseDto(payment.getOrderId(), payment.getTotalAmount());
    }
}
