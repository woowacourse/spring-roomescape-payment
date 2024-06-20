package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationFixture {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation createPastReservation(ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 1), time, theme, member, ReservationStatus.BOOKED);
        return reservationRepository.save(reservation);
    }

    public Reservation createFutureReservation(ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 8), time, theme, member, ReservationStatus.BOOKED);
        return reservationRepository.save(reservation);
    }

    public Reservation createPaymentWaitingReservation(ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 8), time, theme, member, ReservationStatus.PAYMENT_WAITING);
        return reservationRepository.save(reservation);
    }

    public Reservation createCanceledReservation(ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 8), time, theme, member, ReservationStatus.CANCELED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findAllReservation() {
        return reservationRepository.findAll();
    }
}
