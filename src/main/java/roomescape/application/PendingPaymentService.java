package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.pendingpayment.PendingPaymentRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.exception.NotFoundException;
import roomescape.presentation.response.ReservedResponse;

@Service
@RequiredArgsConstructor
public class PendingPaymentService {

    private final PaymentService paymentService;
    private final ReservedRepository reservedRepository;
    private final PendingPaymentRepository pendingPaymentRepository;

    @Transactional
    public ReservedResponse completePayment(final Long reservationId, final PaymentInfo paymentInfo) {
        PendingPayment pendingPayment = pendingPaymentRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 결제 대기입니다."));

        Payment payment = paymentService.savePayment(paymentInfo);

        Reserved reserved = reservedRepository.save(Reserved.fromPendingPayment(pendingPayment, payment));
        pendingPaymentRepository.deleteById(pendingPayment.getId());
        return ReservedResponse.fromReservation(reserved);
    }
}
