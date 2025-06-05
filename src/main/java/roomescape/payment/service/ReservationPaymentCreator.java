package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.CreateRegistrationCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPaymentCreator {

    private final PaymentRepository paymentRepository;

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Transactional
    public void saveReservationAndPayment(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.debug("ReservationPaymentRequest: {}", request);
        log.debug("LoginMember: {}", loginMember);

        final ReservationResponse reservationResponse = reservationService.registerReservation(
                new CreateRegistrationCommand(loginMember.id(), request.date(), request.timeId(), request.themeId())
        );
        final Reservation reservation = getReservationById(reservationResponse.id());
        log.debug("Reservation ID: {}", reservation.getId());

        final Payment payment = createPendingPayment(request, reservation);
        paymentRepository.save(payment);
    }

    private Payment createPendingPayment(ReservationPaymentRequest request, Reservation reservation) {
        return Payment.builder()
                .paymentKey(request.paymentKey())
                .orderId(request.orderId())
                .amount(request.amount())
                .reservation(reservation)
                .member(reservation.getMember())
                .status(PaymentStatus.PENDING)
                .build();
    }

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다, id:" + id));
    }
}
