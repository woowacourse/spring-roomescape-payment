package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;

    @Transactional
    public void savePayment(final ReservationPaymentRequest request, final LoginMember loginMember) {
        log.debug("ReservationPaymentRequest: {}", request);
        log.debug("LoginMember: {}", loginMember);

        final ReservationResponse reservationResponse = reservationService.resisterReservation(request.toReservationRequest(), loginMember);
        final Reservation reservation = reservationService.findById(reservationResponse.id());
        log.debug("Reservation ID: {}", reservation.getId());

        final Payment payment = Payment.builder()
                .paymentKey(request.paymentKey())
                .orderId(request.orderId())
                .amount(request.amount())
                .reservation(reservation)
                .member(reservation.getMember())
                .build();
        paymentRepository.save(payment);
    }
}
