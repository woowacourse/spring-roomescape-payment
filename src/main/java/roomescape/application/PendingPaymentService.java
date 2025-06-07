package roomescape.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.pendingpayment.PendingPaymentRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.exception.NotFoundException;
import roomescape.presentation.response.ReservedResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingPaymentService {

    private final PaymentService paymentService;
    private final ReservedRepository reservedRepository;
    private final PendingPaymentRepository pendingPaymentRepository;

    @Transactional
    public ReservedResponse confirmPayment(final Long pendingPaymentId, final PaymentInfo paymentInfo) {
        log.info("결제 요청 호출 - pendingPaymentId: {}, peymentKey= {}", pendingPaymentId, paymentInfo.paymentKey());

        PendingPayment pendingPayment = pendingPaymentRepository.findById(pendingPaymentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 결제 대기입니다."));

        Reserved reserved = convertToReserved(pendingPayment);

        paymentService.requestPayment(reserved, paymentInfo);

        return ReservedResponse.fromReservation(reserved);
    }

    private Reserved convertToReserved(final PendingPayment pendingPayment) {
        Reserved reserved = reservedRepository.save(Reserved.fromPendingPayment(pendingPayment));
        pendingPaymentRepository.deleteById(pendingPayment.getId());

        return reserved;
    }
}
