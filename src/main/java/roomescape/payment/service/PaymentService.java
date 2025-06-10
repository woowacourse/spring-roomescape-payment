package roomescape.payment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.logging.LogContent;
import roomescape.global.logging.LogExecution;
import roomescape.global.logging.LogLevel;
import roomescape.payment.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    @LogExecution(
            description = "결제 승인 요청",
            content = {LogContent.REQUEST, LogContent.RESPONSE, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.INFO
    )
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TossPaymentResponse approvePayment(final String orderId, final String paymentKey, final long amount) {
        return paymentClient.requestPaymentApprove(
                new TossPaymentRequest(orderId, paymentKey, amount));
    }

    @Transactional
    public void savePayment(final Reservation reservation, final TossPaymentResponse tossPaymentResponse) {
        paymentRepository.save(new Payment(reservation.getId(), tossPaymentResponse.orderId(), tossPaymentResponse.paymentKey(),
                tossPaymentResponse.totalAmount(), tossPaymentResponse.type()));
    }

    @Transactional(readOnly = true)
    public Map<Long, Payment> getPaymentsByReservationIds(final List<Long> reservationIds) {
        List<Payment> payments = paymentRepository.findByReservationIdIn(reservationIds);
        return payments.stream()
                .collect(Collectors.toMap(
                        Payment::getReservationId,
                        payment -> payment,
                        (existing, replacement) -> existing
                ));
    }
}
