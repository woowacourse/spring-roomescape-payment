package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.TossPayment;
import roomescape.domain.payment.repository.TossPaymentRepository;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;
import roomescape.infrastructure.error.exception.PaymentException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentQueryService {

    private final TossPaymentRepository tossPaymentRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public Map<Long, PaymentResult> getAllPaymentResultsByReservationIds(final List<Long> reservationIds) {
        final List<ReservationPayment> reservationPayments =
                reservationPaymentRepository.findAllByReservationIdIn(reservationIds);

        final List<Long> paymentIds = reservationPayments.stream()
                .filter(ReservationPayment::isTossPayment)
                .map(ReservationPayment::getPaymentId)
                .toList();

        final Map<Long, TossPayment> tossPaymentById = tossPaymentRepository.findAllById(paymentIds).stream()
                .collect(Collectors.toMap(TossPayment::getId, Function.identity()));

        return reservationPayments.stream()
                .collect(Collectors.toMap(
                        ReservationPayment::getReservationId,
                        rp -> getPaymentResult(rp, tossPaymentById)
                ));
    }

    private PaymentResult getPaymentResult(
            final ReservationPayment reservationPayment,
            final Map<Long, TossPayment> tossPaymentById
    ) {
        if (reservationPayment.isTossPayment()) {
            return getTossPaymentResult(reservationPayment, tossPaymentById);
        }
        if (reservationPayment.isAdminPayment()) {
            return getAdminPaymentResult(reservationPayment);
        }
        log.error("지원하지 않는 결제 유형 - paymentType: {}", reservationPayment.getPaymentType());
        throw new PaymentException("존재하지 않는 결제입니다");
    }

    private PaymentResult getTossPaymentResult(
            final ReservationPayment reservationPayment,
            final Map<Long, TossPayment> tossPaymentById
    ) {
        final TossPayment toss = tossPaymentById.get(reservationPayment.getPaymentId());
        if (toss == null) {
            log.error("TossPayment를 찾을 수 없음 - paymentId: {}", reservationPayment.getPaymentId());
            throw new PaymentException("존재하지 않는 결제입니다");
        }
        if (toss.isApproved()) {
            return new PaymentResult(PaymentType.TOSS, toss.getPaymentKey(), toss.getAmount());
        }
        return new PaymentResult(PaymentType.TOSS, toss.getStatusDescription(), toss.getAmount());
    }

    private PaymentResult getAdminPaymentResult(final ReservationPayment reservationPayment) {
        return new PaymentResult(PaymentType.ADMIN, reservationPayment.getPaymentId() + "번 관리자", 0L);
    }
}

