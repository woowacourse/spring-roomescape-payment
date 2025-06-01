package roomescape.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.dto.response.PaymentSuccessResponse;

import java.util.NoSuchElementException;

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
        paymentRepository.save(new Payment(response.paymentKey(), amount, reservationId));
    }

    @Transactional(readOnly = true)
    public Payment getByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 결제 정보입니다."));
    }
}
