package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentDto;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationResponse;

@Service
public class ReservationFacadeService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationFacadeService(final ReservationService reservationService,
                                    final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Transactional(rollbackFor = {Exception.class})
    public ReservationResponse createReservation(final ReservationDto reservationDto, final PaymentDto paymentDto) {
        final ReservationResponse reservationResponse = reservationService.createReservation(reservationDto);
        paymentService.confirmPayment(paymentDto, reservationResponse.id());
        return reservationResponse;
    }

    @Transactional(rollbackFor = {Exception.class})
    public ReservationResponse createAdminReservation(final ReservationDto reservationDto) {
        final ReservationResponse reservationResponse = reservationService.createReservation(reservationDto);
        paymentService.createPayment(reservationResponse.id());
        return reservationResponse;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void cancelReservation(final Long id) {
        final Reservation reservation = reservationService.findReservationById(id);
        if (reservation.isReserved()) {
            paymentService.cancelPayment(reservation);
        }
        reservationService.cancelReservation(reservation);
    }
}
