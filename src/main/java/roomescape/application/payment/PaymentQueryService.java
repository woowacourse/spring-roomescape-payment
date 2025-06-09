package roomescape.application.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.AdminPayment;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.TossPayment;
import roomescape.domain.payment.repository.AdminPaymentRepository;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.domain.payment.repository.TossPaymentRepository;
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

    private final PaymentRepository paymentRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final AdminPaymentRepository adminPaymentRepository;

    public Map<Long, PaymentResult> getAllPaymentResultsByReservationIds(final List<Long> reservationIds) {
        final List<ReservationPayment> reservationPayments =
                reservationPaymentRepository.findAllByReservationIdIn(reservationIds);

        final List<Long> paymentIds = reservationPayments.stream()
                .map(ReservationPayment::getPaymentId)
                .toList();

        final Map<Long, Payment> paymentById = paymentRepository.findAllById(paymentIds).stream()
                .collect(Collectors.toMap(Payment::getId, Function.identity()));

        final Map<Long, TossPayment> tossPaymentById = tossPaymentRepository.findAllByPaymentIdIn(paymentIds).stream()
                .collect(Collectors.toMap(TossPayment::getPaymentId, Function.identity()));

        final Map<Long, AdminPayment> adminPaymentById = adminPaymentRepository.findAllByPaymentIdIn(paymentIds).stream()
                .collect(Collectors.toMap(AdminPayment::getPaymentId, Function.identity()));

        return reservationPayments.stream()
                .collect(Collectors.toMap(
                        ReservationPayment::getReservationId,
                        getReservationResult(paymentById, tossPaymentById, adminPaymentById)
                ));
    }

    private Function<ReservationPayment, PaymentResult> getReservationResult(
            final Map<Long, Payment> paymentById,
            final Map<Long, TossPayment> tossPaymentById,
            final Map<Long, AdminPayment> adminPaymentById) {
        return reservationPayment -> {
            final Payment payment = paymentById.get(reservationPayment.getPaymentId());

            if (payment.isTossPayment()) {
                final TossPayment toss = tossPaymentById.get(reservationPayment.getPaymentId());
                if (toss == null) {
                    log.error("TossPayment를 찾을 수 없음 - paymentId: {}", reservationPayment.getPaymentId());
                    throw new PaymentException("존재하지 않는 결제입니다");
                }
                return PaymentResult.from(toss);
            }

            if (payment.isAdminPayment()) {
                final AdminPayment admin = adminPaymentById.get(reservationPayment.getPaymentId());
                if (admin == null) {
                    log.error("AdminPayment를 찾을 수 없음 - paymentId: {}", reservationPayment.getPaymentId());
                    throw new PaymentException("존재하지 않는 결제입니다");
                }
                return PaymentResult.from(admin);
            }

            log.error("지원하지 않는 결제 유형 - paymentType: {}", payment.getPaymentType());
            throw new PaymentException("존재하지 않는 결제입니다");
        };
    }
}

