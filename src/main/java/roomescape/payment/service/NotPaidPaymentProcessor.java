package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;

@Component
@RequiredArgsConstructor
public class NotPaidPaymentProcessor {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Payment prepareNotPaidToPending(final Long reservationId,
                                           final PaymentRequest request,
                                           final LoginMember loginMember) {
        validateReservationOwnership(reservationId, loginMember);

        Payment payment = getPaymentByReservationId(reservationId);
        validateCanProcessStatus(payment);
        payment.assignPaymentInformation(request.paymentKey(), request.orderId(), request.amount());
        payment.updateStatusTo(PaymentStatus.PENDING);
        return payment;
    }

    private void validateReservationOwnership(Long reservationId, LoginMember loginMember) {
        Reservation reservation = getReservationById(reservationId);
        if (reservation.isOwnedBy(loginMember.id())) {
            return;
        }
        throw new TossPaymentException(HttpStatus.BAD_REQUEST, "본인의 예약만 결제할 수 있습니다.", false);
    }

    private void validateCanProcessStatus(Payment payment) {
        if (payment.getStatus() != PaymentStatus.NOT_PAID) {
            throw new TossPaymentException(HttpStatus.BAD_REQUEST,
                    "결제를 시작할 수 있는 상태가 아닙니다, 현재 상태: " + payment.getStatus().getDescription(), false);
        }
    }

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다, id:" + id));
    }

    private Payment getPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        "reservationId에 해당하는 결제를 찾을 수 없습니다, reservationId: " + reservationId));
    }
}
