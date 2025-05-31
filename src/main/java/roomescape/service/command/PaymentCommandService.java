package roomescape.service.command;

import org.springframework.stereotype.Service;
import roomescape.client.TossPaymentClient;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;

@Service
public class PaymentCommandService {

    private final TossPaymentClient tossPaymentClient;

    public PaymentCommandService(TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public void confirmPayment(TossPaymentConfirmRequestDto requestDto) {
        tossPaymentClient.confirmPayment(requestDto);
    }
}
