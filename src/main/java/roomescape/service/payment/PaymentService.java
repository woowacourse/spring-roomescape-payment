package roomescape.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.dto.response.PaymentSuccessResponse;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentApproveClient paymentApproveClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void approveAndSave(String paymentKey, String orderId, int amount, Long reservationId) {
        final PaymentSuccessResponse response = paymentApproveClient.approvePayment(
                paymentKey,
                orderId,
                amount
        );
        paymentRepository.save(new Payment(reservationId, response.paymentKey()));
    }
}
