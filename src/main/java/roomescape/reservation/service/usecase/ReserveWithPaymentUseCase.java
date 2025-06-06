package roomescape.reservation.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.toss.domain.TossPayment;
import roomescape.payment.toss.service.TossPaymentService;

@Service
@RequiredArgsConstructor
public class ReserveWithPaymentUseCase {

    private final TossPaymentService tossPaymentService;

    public void execute(final TossPayment tossPayment) {
        tossPaymentService.confirmPayment(tossPayment);
    }
}
