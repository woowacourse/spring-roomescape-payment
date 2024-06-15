package roomescape.reservation.application;

import roomescape.member.domain.Member;
import roomescape.payment.application.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

public class DummyReservationManageService extends ReservationManageService {
    public DummyReservationManageService(ReservationRepository reservationRepository, PaymentService paymentService) {
        super(reservationRepository, paymentService);
    }

    @Override
    protected void correctReservationStatus(int bookingCount, Reservation reservation) {
    }

    @Override
    protected void scheduleAfterDeleting(Reservation deletedReservation) {
    }

    @Override
    protected void validateReservationStatus(Reservation reservation) {
    }

    @Override
    protected void validatePermissionForDeleting(Reservation reservation, Member agent) {
    }
}
