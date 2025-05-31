package roomescape.service.command;

import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.dto.reservation.PaymentConfirmRequestDto;

@Service
public class PaymentCommandService {

    private final PaymentClient paymentClient;

    public PaymentCommandService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void confirmPayment(PaymentConfirmRequestDto requestDto) {
        paymentClient.confirmPayment(requestDto);
    }
}
